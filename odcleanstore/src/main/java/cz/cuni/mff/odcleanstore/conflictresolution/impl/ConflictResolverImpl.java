package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationType;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver;
import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.AggregationMethod;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.AggregationMethodFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.AggregationNotImplementedException;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.QuadGraph;
import cz.cuni.mff.odcleanstore.graph.Triple;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.shared.TripleItemComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the conflict resolution process.
 * 
 * @author Jan Michelfeit
 */
public class ConflictResolverImpl implements ConflictResolver {
    private static final Logger LOG = LoggerFactory.getLogger(ConflictResolverImpl.class);
    
    /**
     * Settings for the conflict resolution process.
     */
    private final ConflictResolverSpec spec;
    
    /**
     * Comparator of {@link Quad Quads} comparing first by objects, second
     * by data source in metadata, third by descending stored date in metadata.
     */
    protected static class ObjectSourceComparator implements Comparator<Quad> {
        /** Metadata for named graphs occuring in compared quads. */
        private NamedGraphMetadataMap namedGraphMetadata;
        
        /**
         * @param metadata metadata for named graphs occuring in compared quads;
         *      must not be null
         */
        public ObjectSourceComparator(NamedGraphMetadataMap metadata) {
            assert metadata != null;
            this.namedGraphMetadata = metadata;
        }
        
        @Override
        public int compare(Quad o1, Quad o2) {
            // Compare by object
            int objectComparison = TripleItemComparator.compare(
                    o1.getObject(),
                    o2.getObject());
            if (objectComparison != 0) {
                return objectComparison;
            }
            
            // Get metadata
            NamedGraphMetadata metadata1 = namedGraphMetadata.getMetadata(o1.getNamedGraph());
            NamedGraphMetadata metadata2 = namedGraphMetadata.getMetadata(o2.getNamedGraph());
            
            // Compare by data source
            String dataSource1 = (metadata1 != null) ? metadata1.getDataSource() : null;
            String dataSource2 = (metadata2 != null) ? metadata2.getDataSource() : null;
            if (dataSource1 == null && dataSource2 == null) {
                return 0;
            } else if (dataSource1 == null) {
                return -1;
            } else if (dataSource2 == null) {
                return 1;
            }
            int dataSourceComparison = dataSource1.compareTo(dataSource2);
            if (dataSourceComparison != 0) {
                return dataSourceComparison;
            }
            
            // Compare by stored time in *descending order*
            Date stored1 = (metadata1 != null) ? metadata1.getStored() : null;
            Date stored2 = (metadata2 != null) ? metadata2.getStored() : null;
            if (stored1 == null || stored2 == null) {
                if (stored1 == stored2) { // intentionally == - comparing to null
                    return 0;
                } else if (stored1 == null) {
                    return 1;
                } else if (stored2 == null) {
                    return -1;
                }
            }
            return stored2.compareTo(stored1);
        }
    }
    
    /**
     * Creates a new instance of conflict resolver for settings passed in spec.
     * @param spec settings for the conflict resolution process
     */
    public ConflictResolverImpl(ConflictResolverSpec spec) {
        this.spec = spec;
    }
    
    /**
     * {@inheritDoc}
     * 
     * @param data {@inheritDoc }
     * @return {@inheritDoc }
     * @throws ODCleanStoreException {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> resolveConflicts(QuadGraph data) throws ODCleanStoreException {
        LOG.info("Resolving conflicts among {} quads.", data.size());
        
        // Apply owl:sameAs mappings, group quads to conflict clusters
        ResolveQuadCollection quadsToResolve = new ResolveQuadCollection();
        quadsToResolve.addQuads(data);
        URIMappingImpl uriMappings = new URIMappingImpl(spec.getPreferredURIs());
        uriMappings.addLinks(getSameAsLinks(data));
        quadsToResolve.applyMapping(uriMappings);

        // Gather relevant settings:
        NamedGraphMetadataMap metadata = getNamedGraphMetadata(data);
        AggregationErrorStrategy aggregationErrorStrategy = spec.getErrorStrategy();
        UniqueURIGenerator uriGenerator = new SimpleUriGenerator(spec.getNamedGraphPrefix());
        
        // A little optimization - check metadata for occurences of old versions;
        // if there are none, there is no need to try to filter them in each
        // conflict cluster.
        boolean hasOldVersions = hasOldVersions(metadata);
        ObjectSourceComparator filterComparator = hasOldVersions
                ? new ObjectSourceComparator(metadata)
                : null;
        if (hasOldVersions) {
            LOG.info("Resolved data include named graphs with multiple versions");
        }
        
        // Resolve conflicts:
        Collection<CRQuad> result = createResultCollection();
        Iterator<Collection<Quad>> conflictIterator = quadsToResolve.listConflictingQuads();
        while (conflictIterator.hasNext()) {
            // Process next set of conflicting quads independently
            Collection<Quad> conflictCluster = conflictIterator.next(); 
            
            if (hasOldVersions) {
                conflictCluster = filterOldVersions(conflictCluster, metadata, filterComparator);
            }
            
            AggregationMethod aggregator = getAggregator(conflictCluster);
            Collection<CRQuad> aggregatedQuads = aggregator.aggregate(
                    conflictCluster,
                    metadata, 
                    aggregationErrorStrategy,
                    uriGenerator);
            
            // Add resolved quads to result
            result.addAll(aggregatedQuads);
        }
        return result;
    }
    
    /**
     * Removes duplicate triples that are remaining from older versions of the 
     * same named graph.
     * A triple from named graph A is removed iff
     * <ul>
     *  <li>(1) it is identical to another triple from a different named graph B,</li>
     *  <li>(2) named graphs A and B have the same data source in metadata,</li>
     *  <li>(3) named graph A has an older stored date than named graph B.</li>
     * </ul>
     * 
     * Current implementation has O(n log^2 n) time complexity.
     * 
     * @param conflictingQuads a cluster of conflicting quads (quads having 
     *      the same subject and predicate)
     * @param metadata metadata for named graphs occuring in conflictingQuads
     * @param objectSourceComparator instance of {@link ObjectSourceComparator}
     *      for metadata related to conflictingQuads; passed as a parameter,
     *      so that a new comparator instance doesn't have to be created for
     *      each cluster of conflicting quads
     * @return collection of quads where duplicate old version triples are removed
     */
    private Collection<Quad> filterOldVersions(
            Collection<Quad> conflictingQuads, 
            NamedGraphMetadataMap metadata,
            ObjectSourceComparator  objectSourceComparator) {
        
        // Sort quads by object, data source and time in *reverse order*.
        // Since for every comparison we search the metadata map in
        // logarithmical time, sorting has time complexity O(n log^2 n)
        LinkedList<Quad> result = new LinkedList<Quad>(conflictingQuads);
        Collections.sort(result, objectSourceComparator);
        
        // Remove unwanted quads in one pass
        TripleItem lastObject = null;
        String lastNamedGraph = null;
        Iterator<Quad> resultIterator = result.iterator();
        while (resultIterator.hasNext()) {
            boolean removed = false;
            Quad quad = resultIterator.next();
            if (quad.getObject().equals(lastObject) 
                    && !quad.getNamedGraph().equals(lastNamedGraph)) {
                // (1) holds
                NamedGraphMetadata lastMetadata = metadata.getMetadata(lastNamedGraph);
                NamedGraphMetadata quadMetadata = metadata.getMetadata(quad.getNamedGraph());
                if (lastMetadata != null
                        && quadMetadata != null
                        && quadMetadata.getDataSource() != null 
                        && quadMetadata.getDataSource().equals(lastMetadata.getDataSource())
                        && quadMetadata.getStored() != null
                        && lastMetadata.getStored() != null
                        && quadMetadata.getStored().before(lastMetadata.getStored())) {
                    // (2) and (3) holds
                    resultIterator.remove();
                    removed = true;
                    LOG.debug("Filtered a triple from an outdated named graph {}.", 
                            quad.getNamedGraph());
                }
            }
            
            if (!removed) {
                lastObject = quad.getObject();
                lastNamedGraph = quad.getNamedGraph();
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
     *      an update of the other
     */
    private boolean hasOldVersions(NamedGraphMetadataMap metadataMap) {
        Collection<NamedGraphMetadata> metadataCollection = metadataMap.listMetadata();
        Map<String, Date> dataSourceDates = 
                new HashMap<String, Date>(metadataCollection.size());
        
        for (NamedGraphMetadata metadata : metadataCollection) {
            assert metadata != null;
            String dataSource = metadata.getDataSource();
            
            if (dataSourceDates.containsKey(dataSource)
                    && !dataSourceDates.get(dataSource).equals(metadata.getStored())) {
                // Occurence of named graphs sharing a common data source 
                // with a different stored date
                return true;
            } else if (dataSource != null && metadata.getStored() != null) {
                dataSourceDates.put(dataSource, metadata.getStored());
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
     * Returns an iterator over owl:sameAs links (expressed as {@link Triple Triples})
     * according to {@linkplain ConflictResolverSpec conflict resolution settings}.
     * @param data graph of triples where conflicts are to be resolved
     * @return an iterator over owl:sameAs links
     */
    private Iterator<Triple> getSameAsLinks(Iterable<? extends Triple> data) {
        Iterator<Triple> specSameAsLinks = spec.getSameAsLinks();
        if (specSameAsLinks != null) {
            return specSameAsLinks;
        } else {
            return new SameAsLinkIterator(data);
        }
    }

    /**
     * Returns named graph metadata from
     * {@linkplain ConflictResolverSpec conflict resolution settings}.
     * If no metadata are specified, tries to read them from RDF data to resolve
     * @param data graph of triples where conflicts are to be resolved
     * @return named graphs' metadata
     * @throws ODCleanStoreException thrown when named graph metadata contained 
     *      in the input graph are not correctly formated
     */
    private NamedGraphMetadataMap getNamedGraphMetadata(QuadGraph data) 
            throws ODCleanStoreException {
        
        NamedGraphMetadataMap metadata = spec.getNamedGraphMetadata();
        if (metadata != null) {
            return metadata;
        } else {
            return NamedGraphMetadataReader.readFromRDF(data);
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
     *      subject and predicate)
     * @return an aggregation method instance selected according to CR settings 
     * @throws AggregationNotImplementedException thrown if there is no 
     *      AggregationMethod implementation for the selected aggregation type
     * @todo consider sameAs links?
     * @todo caching?
     */
    private AggregationMethod getAggregator(Collection<Quad> quads) 
            throws AggregationNotImplementedException {
        
        if (quads.size() == 1) {
            // A little optimization: behavior of all aggregation method on 
            // a single quad is supposed to be the same, so we can use an
            // instance optimized for single values
            return AggregationMethodFactory.getSingleValueAggregation();
        }
        String clusterProperty = getQuadsProperty(quads);
        AggregationType propertyAggregation = spec.propertyAggregationType(clusterProperty);
        return AggregationMethodFactory.getAggregation(propertyAggregation);
    }
    
    /**
     * Returns URI of the predicate used in a collection of conflicting quads.
     * Asserts that all quads in the collection have the same predicate.
     * @param quads collection of conflicting quads (i.e. having the same
     *      subject and predicate)
     * @return URI of the predicate occuring in the quads 
     */
    private String getQuadsProperty(Collection<Quad> quads) {
        assert !quads.isEmpty() : "Set of conflicting quads must be nonempty";
        Quad firstQuad = quads.iterator().next();
        assert firstQuad.getPredicate() instanceof URITripleItem;
        return firstQuad.getPredicate().getURI();
    }
}
