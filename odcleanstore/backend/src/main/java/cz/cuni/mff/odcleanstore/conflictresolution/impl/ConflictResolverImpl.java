package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.AggregationMethod;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.AggregationMethodFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.AggregationNotImplementedException;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;
import cz.cuni.mff.odcleanstore.shared.NodeComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import cz.cuni.mff.odcleanstore.shared.Utils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Default implementation of the conflict resolution process.
 * Non-static methods are not thread-safe (shared {@link #aggregationFactory}).
 *
 * @author Jan Michelfeit
 */
public class ConflictResolverImpl implements ConflictResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ConflictResolverImpl.class);

    /**
     * An AggregationMethod factory.
     */
    private AggregationMethodFactory aggregationFactory;

    /**
     * Settings for the conflict resolution process.
     */
    private final ConflictResolverSpec spec;

    /**
     * Comparator of {@link Quad Quads} comparing first by objects, second
     * by data source in metadata, third by descending stored date in metadata.
     */
    protected static class ObjectSourceStoredComparator implements Comparator<Quad> {
        /** Metadata for named graphs occurring in compared quads. */
        private NamedGraphMetadataMap namedGraphMetadata;

        /**
         * @param metadata metadata for named graphs occurring in compared quads; must not be null
         */
        public ObjectSourceStoredComparator(NamedGraphMetadataMap metadata) {
            assert metadata != null;
            this.namedGraphMetadata = metadata;
        }

        @Override
        public int compare(Quad q1, Quad q2) {
            // Compare by object
            int objectComparison = NodeComparator.compare(q1.getObject(), q2.getObject());
            if (objectComparison != 0) {
                return objectComparison;
            }

            // Get metadata
            NamedGraphMetadata metadata1 = namedGraphMetadata.getMetadata(q1.getGraphName());
            NamedGraphMetadata metadata2 = namedGraphMetadata.getMetadata(q2.getGraphName());

            // Compare by data source
            String dataSource1 = (metadata1 != null) ? metadata1.getSource() : null;
            String dataSource2 = (metadata2 != null) ? metadata2.getSource() : null;
            int dataSourceComparison = Utils.nullProofCompare(dataSource1, dataSource2);
            if (dataSourceComparison != 0) {
                return dataSourceComparison;
            }

            // Compare by stored time in *descending order*
            Date stored1 = (metadata1 != null) ? metadata1.getInsertedAt() : null;
            Date stored2 = (metadata2 != null) ? metadata2.getInsertedAt() : null;
            return Utils.nullProofCompare(stored2, stored1); // switched arguments
        }
    }

    /**
     * Creates a new instance of conflict resolver for settings passed in spec.
     * @param spec settings for the conflict resolution process
     */
    public ConflictResolverImpl(ConflictResolverSpec spec) {
        this.spec = spec;
        UniqueURIGenerator uriGenerator = new SimpleUriGenerator(spec.getNamedGraphPrefix());
        this.aggregationFactory = new AggregationMethodFactory(uriGenerator, spec);
    }

    /**
     * {@inheritDoc}
     *
     * @param quads {@inheritDoc }
     * @return {@inheritDoc }
     * @throws ConflictResolutionException {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> resolveConflicts(Collection<Quad> quads) throws ConflictResolutionException {
        LOG.info("Resolving conflicts among {} quads.", quads.size());
        long startTime = System.currentTimeMillis();

        // Apply owl:sameAs mappings, group quads to conflict clusters
        ResolveQuadCollection quadsToResolve = new ResolveQuadCollection();
        quadsToResolve.addQuads(quads);
        URIMappingImpl uriMappings = new URIMappingImpl(spec.getPreferredURIs());
        uriMappings.addLinks(getSameAsLinks(quads));
        quadsToResolve.applyMapping(uriMappings);

        // Get metadata:
        NamedGraphMetadataMap metadata = getNamedGraphMetadata(quads);

        // A little optimization - check metadata for occurrences of old versions;
        // if there are none, there is no need to try to filter them in each
        // conflict cluster.
        boolean hasOldVersions = hasOldVersions(metadata);
        ObjectSourceStoredComparator filterComparator = hasOldVersions
                ? new ObjectSourceStoredComparator(metadata)
                : null;
        if (hasOldVersions) {
            LOG.info("Resolved data include named graphs with multiple versions");
        }

        // Resolve conflicts:
        Collection<CRQuad> result = createResultCollection();
        Iterator<Collection<Quad>> conflictIterator = quadsToResolve.listConflictingQuads();
        while (conflictIterator.hasNext()) {
            // Process the next set of conflicting quads independently
            Collection<Quad> conflictCluster = conflictIterator.next();

            if (hasOldVersions && conflictCluster.size() > 1) {
                conflictCluster = filterOldVersions(conflictCluster, metadata, filterComparator);
            }

            AggregationMethod aggregator = getAggregator(conflictCluster);
            Collection<CRQuad> aggregatedQuads = aggregator.aggregate(conflictCluster, metadata);

            // Add resolved quads to result
            result.addAll(aggregatedQuads);
        }

        LOG.debug("Conflict resolution executed in {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    /**
     * Removes duplicate triples that are remaining from older versions of the
     * same named graph.
     * A triple from named graph A is removed iff
     * <ul>
     * <li>(1) it is identical to another triple from a different named graph B,</li>
     * <li>(2) named graphs A and B have the same data source in metadata,</li>
     * <li>(3) named graph A has an older stored date than named graph B,</li>
     * <li>(4) named graphs A and B were inserted by the same user.</li>
     * </ul>
     *
     * The current implementation has O(n log^2 n) time complexity.
     *
     * @param conflictingQuads a cluster of conflicting quads (quads having
     *        the same subject and predicate)
     * @param metadata metadata for named graphs occurring in conflictingQuads
     * @param objectSourceStoredComparator instance of {@link ObjectSourceStoredComparator} for
     *        metadata related to conflictingQuads; passed as a parameter, so that a new comparator
     *        instance doesn't have to be created for each cluster of conflicting quads
     * @return collection of quads where duplicate old version triples are removed
     */
    private Collection<Quad> filterOldVersions(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            ObjectSourceStoredComparator objectSourceStoredComparator) {

        // Sort quads by object, data source and time in *reverse order*.
        // Since for every comparison we search the metadata map in
        // logarithmic time, sorting has time complexity O(n log^2 n)
        LinkedList<Quad> result = new LinkedList<Quad>(conflictingQuads);
        Collections.sort(result, objectSourceStoredComparator);

        // Remove unwanted quads in one pass
        Node lastObject = null;
        Node lastNamedGraph = null;
        Iterator<Quad> resultIterator = result.iterator();
        while (resultIterator.hasNext()) {
            boolean removed = false;
            Quad quad = resultIterator.next();
            if (quad.getObject().sameValueAs(lastObject) && !quad.getGraphName().sameValueAs(lastNamedGraph)) {
                // (1) holds
                NamedGraphMetadata lastMetadata = metadata.getMetadata(lastNamedGraph);
                NamedGraphMetadata quadMetadata = metadata.getMetadata(quad.getGraphName());
                if (lastMetadata != null
                        && quadMetadata != null
                        && quadMetadata.getSource() != null
                        && quadMetadata.getSource().equals(lastMetadata.getSource()) // (2) holds
                        && quadMetadata.getInsertedAt() != null
                        && lastMetadata.getInsertedAt() != null
                        && quadMetadata.getInsertedAt().before(lastMetadata.getInsertedAt()) // (3) holds
                        && quadMetadata.getInsertedBy() != null
                        && quadMetadata.getInsertedBy().equals(lastMetadata.getInsertedBy())) { // (4) holds
                    resultIterator.remove();
                    removed = true;
                    LOG.debug("Filtered a triple from an outdated named graph {}.", quad.getGraphName().getURI());
                }
            }

            if (!removed) {
                lastObject = quad.getObject();
                lastNamedGraph = quad.getGraphName();
            }
        }

        return result;
    }

    /**
     * Check whether metadata contain two named graphs where one is
     * an update of the other.
     * A named graph is an update of another graph if it has the same data
     * source but a newer stored date.
     *
     * @param metadataMap named graph metadata to analyze
     * @return true iff metadata contain two named graphs where one is
     *         an update of the other
     */
    private boolean hasOldVersions(NamedGraphMetadataMap metadataMap) {
        Collection<NamedGraphMetadata> metadataCollection = metadataMap.listMetadata();
        Map<String, Date> dataSourceDates = new HashMap<String, Date>(metadataCollection.size());

        for (NamedGraphMetadata metadata : metadataCollection) {
            assert metadata != null;
            String dataSource = metadata.getSource();

            if (dataSourceDates.containsKey(dataSource)
                    && !dataSourceDates.get(dataSource).equals(metadata.getInsertedAt())) {
                // Occurrence of named graphs sharing a common data source
                // with a different stored date
                return true;
            } else if (dataSource != null && metadata.getInsertedAt() != null) {
                dataSourceDates.put(dataSource, metadata.getInsertedAt());
            }
        }
        return false;
    }

    /**
     * Creates an empty instance of collection that is returned as the output
     * of conflict resolution.
     * @return an empty collection
     */
    private Collection<CRQuad> createResultCollection() {
        return new LinkedList<CRQuad>();
    }

    /**
     * Returns an iterator over owl:sameAs links (expressed as {@link Quad Quads})
     * according to {@linkplain ConflictResolverSpec conflict resolution settings}.
     * @param data graph of triples where conflicts are to be resolved
     * @return an iterator over owl:sameAs links
     */
    private Iterator<Triple> getSameAsLinks(Iterable<? extends Quad> data) {
        Iterator<Triple> specSameAsLinks = spec.getSameAsLinks();
        if (specSameAsLinks != null) {
            return specSameAsLinks;
        } else {
            return new SameAsLinkIterator(data);
        }
    }

    /**
     * Returns named graph metadata from {@linkplain ConflictResolverSpec conflict resolution
     * settings}.
     * If no metadata are specified, tries to read them from RDF data to resolve.
     * @param data collection of quads where conflicts are to be resolved
     * @return named graphs' metadata
     */
    private NamedGraphMetadataMap getNamedGraphMetadata(Collection<Quad> data) {
        NamedGraphMetadataMap metadata = spec.getNamedGraphMetadata();
        if (metadata != null) {
            return metadata;
        } else {
            return NamedGraphMetadataReader.readFromRDF(data.iterator());
        }
    }

    /**
     * Get an AggregationMethod instance for a set of conflicting quads according
     * to {@linkplain ConflictResolverSpec conflict resolution settings}.
     *
     * Implementation note: As an optimization, for collections containing
     * a single quad returns instance of {@link SingleValueAggregation}.
     *
     * @param quads collection of conflicting quads (i.e. having the same
     *        subject and predicate)
     * @return an aggregation method instance selected according to CR settings
     * @throws AggregationNotImplementedException thrown if there is no
     *         AggregationMethod implementation for the selected aggregation type
     */
    private AggregationMethod getAggregator(Collection<Quad> quads) throws AggregationNotImplementedException {
        if (quads.size() == 1) {
            // A little optimization: behavior of all aggregation method on
            // a single quad is supposed to be the same, so we can use an
            // instance optimized for single values
            return aggregationFactory.getSingleValueAggregation();
        }
        String clusterProperty = getQuadsProperty(quads);
        EnumAggregationType propertyAggregation = spec.propertyAggregationType(clusterProperty);
        return aggregationFactory.getAggregation(propertyAggregation);
    }

    /**
     * Returns URI of the predicate used in a collection of conflicting quads.
     * Asserts that all quads in the collection have the same predicate.
     * @param quads collection of conflicting quads (i.e. having the same
     *        subject and predicate)
     * @return URI of the predicate occurring in the quads
     */
    private String getQuadsProperty(Collection<Quad> quads) {
        assert !quads.isEmpty() : "Collection of conflicting quads must be nonempty";
        Quad firstQuad = quads.iterator().next();
        return firstQuad.getPredicate().getURI();
    }
}
