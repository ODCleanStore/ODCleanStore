package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.LexicalLengthComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.ReverseAggregationComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Aggregation method that returns the quad with the shortest lexical form of the object.
 * This aggregation is applicable to quads with a literal as their object.
 * @author Jan Michelfeit
 */
/*package*/final class ShortestAggregation extends BestSelectedAggregation {
    private static final AggregationComparator AGGREGATION_COMPARATOR =
            new ReverseAggregationComparator(new LexicalLengthComparator());

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public ShortestAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    @Override
    protected AggregationComparator getComparator(Collection<Statement> conflictingQuads) {
        return AGGREGATION_COMPARATOR;
    }
}
