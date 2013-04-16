package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.ArrayList;
import java.util.Collection;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuadImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Base class for aggregation methods that select a single best quad based on a given comparison of quads.
 * @author Jan Michelfeit
 */
/*package*/abstract class BestSelectedAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public BestSelectedAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    /**
     * Implementation of {@link #aggregate(Collection, NamedGraphMetadataMap)} that returns the best quad
     * selected from input quads wrapped as CRQuad. The best quad is the quad with the highest order in ordering
     * given by comparator returned by {@link #getComparator(Collection)}.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {
        Collection<CRQuad> result = createResultCollection();
        AggregationComparator comparator = getComparator(conflictingQuads);
        Collection<Statement> aggregableQuads = new ArrayList<Statement>(conflictingQuads.size());
        Statement bestQuad = null; // the best quad so far
        for (Statement quad : conflictingQuads) {
            if (!comparator.isAggregable(quad)) {
                handleNonAggregableObject(quad, conflictingQuads, metadata, result, this.getClass());
                continue;
            }

            aggregableQuads.add(quad);
            if (bestQuad == null || comparator.compare(quad, bestQuad, metadata) > 0) {
                bestQuad = quad;
            }
        }

        if (bestQuad == null) {
            // no aggregable quad
            return result;
        }

        Statement resultQuad = VALUE_FACTORY.createStatement(
                bestQuad.getSubject(),
                bestQuad.getPredicate(),
                bestQuad.getObject(),
                VALUE_FACTORY.createURI(uriGenerator.nextURI()));
        Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(bestQuad.getObject(), aggregableQuads);
        double quality = computeQualitySelected(bestQuad, sourceNamedGraphs, aggregableQuads, metadata);
        result.add(new CRQuadImpl(resultQuad, quality, sourceNamedGraphs));
        return result;
    }

    /**
     * Returns a comparator of quads that orders the best quad as having the highest order.
     * @param conflictingQuads input quads to be aggregated
     * @return a comparator of quads that orders the best quad as having the highest order
     */
    protected abstract AggregationComparator getComparator(Collection<Statement> conflictingQuads);
}