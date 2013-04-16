package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.LiteralComparatorFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Aggregation method that returns the quad with the highest literal value in place of the object.
 * The comparison is chosen based on the type of literal values - e.g. comparing as numeric values,
 * as dates, ... (see {@  AggregationUtils.getComparisonType}).
 * This aggregation is applicable to quads with a literal of the chosen comparison type as their object.
 * @author Jan Michelfeit
 */
/*package*/final class MaxAggregation extends BestSelectedAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public MaxAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    @Override
    protected AggregationComparator getComparator(Collection<Statement> conflictingQuads) {
        EnumLiteralType comparisonType = AggregationUtils.getComparisonType(conflictingQuads);
        if (comparisonType == null) {
            comparisonType = EnumLiteralType.OTHER;
        }
        return LiteralComparatorFactory.getComparator(comparisonType);
    }
}
