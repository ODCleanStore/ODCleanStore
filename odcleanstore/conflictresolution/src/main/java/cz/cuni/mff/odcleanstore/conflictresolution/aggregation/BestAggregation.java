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
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolverImpl;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Aggregation method that returns the quad with the highest (conflict resolution)
 * quality or the newest stored time in case of equality of quality.
 *
 * @author Jan Michelfeit
 */
/*package*/final class BestAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public BestAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    /**
     * Returns a single triple selected from input triples wrapped as CRQuad.
     * Returns a triple with the highest (conflict resolution quality) or the
     * newest stored time in case of equality of quality.
     * If conflictingQuads are empty, returns an empty collection.
     *
     * {@inheritDoc}
     *
     * The time complexity is quadratic with number of conflicting quads.
     * (for each quad calculates its quality in linear time).
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {
        if (conflictingQuads.isEmpty()) {
            return createResultCollection();
        }

        // Sort quads by object so that we can detect identical triples; this way we don't need to call
        // sourceNamedGraphsForObject() for every quad (even though the complexity is still quadratic
        // thanks to quality computation)
        Statement[] sortedQuads = conflictingQuads.toArray(new Statement[0]);
        Arrays.sort(sortedQuads, OBJECT_NG_COMPARATOR);

        Statement bestQuad = null;
        double bestQuadQuality = Double.NEGATIVE_INFINITY;
        Statement lastQuad = null; // quad from the previous iteration
        Value lastObject = null; // lastQuad's object
        ArrayList<String> sourceNamedGraphs = new ArrayList<String>(); // sources for lastQuad
        for (Statement quad : sortedQuads) {
            Value object = quad.getObject();
            boolean isNewObject = !ConflictResolverImpl.crSameValues(object, lastObject);

            if (isNewObject && lastQuad != null) {
                // Do we have a new best quad?
                // CHECKSTYLE:OFF
                double quality = computeQualitySelected(lastQuad, sourceNamedGraphs, conflictingQuads, metadata);
                assert quality != Double.NEGATIVE_INFINITY; // bestQuad won't be null in compareByInsertedAt()
                if (quality > bestQuadQuality
                        || (quality == bestQuadQuality && AggregationUtils.compareByInsertedAt(lastQuad, bestQuad, metadata) > 0)) {
                    bestQuad = lastQuad;
                    bestQuadQuality = quality;
                }
                // CHECKSTYLE:ON
            }

            if (isNewObject) {
                // A new object
                lastQuad = quad;
                lastObject = object;
                sourceNamedGraphs.clear();
                sourceNamedGraphs.add(getSourceGraphURI(quad));
            } else {
                // A quad with object identical to that of the previous quad
                assert lastQuad != null && lastObject != null && sourceNamedGraphs.size() >= 1;
                String lastNamedGraph = sourceNamedGraphs.get(sourceNamedGraphs.size() - 1);
                // Avoid duplicities in sourceNamedGraphs:
                String sourceNamedGraph = getSourceGraphURI(quad);
                if (!sourceNamedGraph.equals(lastNamedGraph)) {
                    sourceNamedGraphs.add(sourceNamedGraph);
                }
            }
        }

        if (lastQuad != null) {
            // Don't forget to check the last quad
            double quality = computeQualitySelected(lastQuad, sourceNamedGraphs, conflictingQuads, metadata);
            assert quality != Double.NEGATIVE_INFINITY; // bestQuad won't be null in compareByInsertedAt()
            // CHECKSTYLE:OFF
            if (quality > bestQuadQuality
                    || (quality == bestQuadQuality && AggregationUtils.compareByInsertedAt(lastQuad, bestQuad, metadata) > 0)) {
                bestQuad = lastQuad;
                bestQuadQuality = quality;
            }
            // CHECKSTYLE:ON
        }

        // bestQuad is not null because conflictingQuads is not empty
        assert bestQuad != null;
        Statement resultQuad = VALUE_FACTORY.createStatement(
                bestQuad.getSubject(),
                bestQuad.getPredicate(),
                bestQuad.getObject(),
                VALUE_FACTORY.createURI(uriGenerator.nextURI()));
        Collection<String> resultSourceNamedGraphs = sourceNamedGraphsForObject(bestQuad.getObject(), conflictingQuads);
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuadImpl(resultQuad, bestQuadQuality, resultSourceNamedGraphs));
        return result;
    }
}
