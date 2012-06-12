package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.AggregationComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators.LiteralComparatorFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Aggregation method that returns the quad with the lowest literal value in place of the object.
 * The comparison is chosen based on the type of literal values - e.g. comparing as numeric values,
 * as dates, ... (see {@  AggregationUtils.getComparisonType}).
 * This aggregation is applicable to quads with a literal of the chosen comparison type as their object.
 * @author Jan Michelfeit
 */
class MinAggregation extends BestSelectedAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public MinAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    @Override
    protected AggregationComparator getComparator(Collection<Quad> conflictingQuads) {
        EnumLiteralType comparisonType = AggregationUtils.getComparisonType(conflictingQuads);
        if (comparisonType == null) {
            comparisonType = EnumLiteralType.OTHER;
        }
        return LiteralComparatorFactory.getReverseComparator(comparisonType);
    }
}
