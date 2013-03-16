package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.GraphQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.GraphQualityComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Aggregation method that returns the quad with the highest total quality of the source of the quad.
 * @author Jan Michelfeit
 */
/*package*/final class BestSourceAggregation extends BestSelectedAggregation {
    private final AggregationComparator aggregationComparator =
            new GraphQualityComparator(new GraphQualityCalculatorImpl());

    /**
     * Implementation of the helper {@link GraphQualityCalculator} interface.
     */
    private final class GraphQualityCalculatorImpl implements GraphQualityCalculator {
        @Override
        public double getSourceQuality(NamedGraphMetadata metadata) {
            return BestSourceAggregation.this.getSourceQuality(metadata);
        }
    }

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public BestSourceAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    @Override
    protected AggregationComparator getComparator(Collection<Quad> conflictingQuads) {
        return aggregationComparator;
    }
}
