package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuadImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolverImpl;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Aggregation method that returns all input triples unchanged except for
 * implicit conflict resolution.
 * Identical triples are aggregated to a single triple with sourceNamedGraphs
 * containing the union of all original named graphs. Otherwise leaves triples
 * as they are and adds a quality estimate.
 *
 * @author Jan Michelfeit
 */
/*package*/final class AllAggregation extends SelectedValueAggregation {
    /**
     * Expected number of data sources per quad - used to initialize data sources collections.
     */
    private static final int EXPECTED_DATA_SOURCES = 2;

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public AllAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    /**
     * Returns all conflicting quads with quads having the same object aggregated, wrapped as CRQuad.
     * If conflictingQuads are empty, returns an empty collection.
     *
     * {@inheritDoc}
     *
     * The time complexity is quadratic with number of conflicting quads
     * (for each quad calculates its quality in linear time).
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {
        Collection<CRQuad> result = createResultCollection();

        // Sort quads by object so that we can detect identical triples
        Statement[] sortedQuads = conflictingQuads.toArray(new Statement[0]);
        Arrays.sort(sortedQuads, OBJECT_NG_COMPARATOR);

        Statement lastQuad = null; // quad from the previous iteration
        Value lastObject = null; // lastQuad's object
        ArrayList<String> sourceNamedGraphs = null; // sources for lastQuad
        for (Statement quad : sortedQuads) {
            Value object = quad.getObject();
            boolean isNewObject = !ConflictResolverImpl.crSameValues(object,  lastObject);

            if (isNewObject && lastQuad != null) {
                // Add lastQuad to result
                Statement resultQuad = VALUE_FACTORY.createStatement(
                        lastQuad.getSubject(),
                        lastQuad.getPredicate(),
                        lastQuad.getObject(),
                        VALUE_FACTORY.createURI(uriGenerator.nextURI()));
                double quadQuality = computeQualitySelected(
                        lastQuad,
                        sourceNamedGraphs,
                        conflictingQuads,
                        metadata);
                result.add(new CRQuadImpl(resultQuad, quadQuality, sourceNamedGraphs));
                sourceNamedGraphs = null;
            }

            if (isNewObject) {
                // A new object
                lastQuad = quad;
                lastObject = object;
                sourceNamedGraphs = new ArrayList<String>(EXPECTED_DATA_SOURCES);
                sourceNamedGraphs.add(getSourceGraphURI(quad));
            } else {
                // A quad with object identical to that of the previous quad
                assert lastQuad != null && lastObject != null;
                assert sourceNamedGraphs != null && sourceNamedGraphs.size() >= 1;
                String lastNamedGraph = sourceNamedGraphs.get(sourceNamedGraphs.size() - 1);
                // Avoid duplicities in sourceNamedGraphs:
                if (!ODCSUtils.nullProofEquals(quad.getContext(), lastNamedGraph)) {
                    sourceNamedGraphs.add(getSourceGraphURI(quad));
                }
            }
        }

        if (lastQuad != null) {
            // Don't forget to add the last quad to result
            Statement resultQuad = VALUE_FACTORY.createStatement(
                    lastQuad.getSubject(),
                    lastQuad.getPredicate(),
                    lastQuad.getObject(),
                    VALUE_FACTORY.createURI(uriGenerator.nextURI()));
            double quadQuality = computeQualitySelected(
                    lastQuad,
                    sourceNamedGraphs,
                    conflictingQuads,
                    metadata);
            result.add(new CRQuadImpl(resultQuad, quadQuality, sourceNamedGraphs));
        }
        return result;
    }
}
