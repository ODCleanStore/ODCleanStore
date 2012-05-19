package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

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
    private static final AggregationComparator AGGREGATION_COMPARATOR =
            new GraphQualityComparator(new GraphQualityCalculatorImpl());

    /**
     * Implementation of the helper {@link GraphQualityCalculator} interface.
     */
    private static final class GraphQualityCalculatorImpl implements GraphQualityCalculator {
        @Override
        public double getSourceQuality(NamedGraphMetadata metadata) {
            return AggregationMethodBase.getSourceQuality(metadata);
        }
    }

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public BestSourceAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    @Override
    protected AggregationComparator getComparator(Collection<Quad> conflictingQuads) {
        return AGGREGATION_COMPARATOR;
    }
}
