package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.SimpleUriGenerator;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.AggregationNotImplementedException;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Factory class for various quad aggregation methods.
 * The factory is implemented as a registry (flyweight) storing and reusing created instances.
 * This relies on the fact that aggregations do not maintain any internal state except for
 * constructor arguments.
 *
 * @author Jan Michelfeit
 */
public class AggregationMethodFactory {
    /**
     * Registry of already created aggregation methods.
     */
    private final Map<EnumAggregationType, ObjectAggregationMethod> methodRegistry =
            new HashMap<EnumAggregationType, ObjectAggregationMethod>();

    /**
     * Generator of URIs passed to newly created aggregations.
     */
    private final UniqueURIGenerator uriGenerator;

    /**
     * Distance metric used in quality calculation.
     */
    private final DistanceMetric distanceMetric;

    /**
     * Global configuration values for conflict resolution.
     */
    private final ConflictResolutionConfig globalConfig;

    /**
     * Creates a new factory with the given settings for creating new aggregations.
     * @param resultGraphPrefix prefix of URIs of named graphs where resolved triples are placed
     * @param globalConfig global configuration values for conflict resolution
     */
    public AggregationMethodFactory(
            String resultGraphPrefix,
            ConflictResolutionConfig globalConfig) {

        this.uriGenerator = new SimpleUriGenerator(resultGraphPrefix);
        this.globalConfig = globalConfig;
        this.distanceMetric = new DistanceMetricImpl(globalConfig);
    }

    /**
     * Returns an instance of AggregationMethod implementing the selected type of aggregation.
     * @param type type of aggregation
     * @param aggregationSpec aggregation settings
     * @return an object implementing the selected aggregation method
     * @throws AggregationNotImplementedException thrown if there is no
     *         AggregationMethod implementation for the selected aggregation type
     * @see EnumAggregationType
     */
    public ObjectAggregationMethod getAggregation(EnumAggregationType type, AggregationSpec aggregationSpec) throws AggregationNotImplementedException {
        ObjectAggregationMethod result = methodRegistry.get(type);
        if (result == null) {
            result = createAggregation(type, aggregationSpec);
            methodRegistry.put(type, result);
        }
        return result;
    }

    /**
     * Returns an instance of AggregationMethod for aggregating a single
     * conflicting quad.
     * Since the behavior of AggregationMethods when
     * aggregating a single value is supposed to be the same, this instance
     * can be used in place of an arbitrary aggregation type on a single quad
     * and possibly provide a better performance than a specialized aggregation
     * method.
     * @param aggregationSpec aggregation settings
     * @return an instance of AggregationMethod
     */
    public ObjectAggregationMethod getSingleValueAggregation(AggregationSpec aggregationSpec) {
        return createSingleValueAggregation(aggregationSpec);
    }

    /**
     * Create a new instance of AggregationMethod implementing the selected type of aggregation.
     * @see #getAggregation(EnumAggregationType)
     * @param type type of aggregation¨
     * @param aggregationSpec aggregation settings
     * @return an new object implementing the selected aggregation method
     * @throws AggregationNotImplementedException thrown if there is no
     *         AggregationMethod implementation for the selected aggregation type
     */
    protected ObjectAggregationMethod createAggregation(EnumAggregationType type, AggregationSpec aggregationSpec)
            throws AggregationNotImplementedException {
        switch (type) {
        case ANY:
            return new AnyAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case ALL:
            return new AllAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case BEST:
            return new BestAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case LATEST:
            return new LatestAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case BEST_SOURCE:
            return new BestSourceAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case TOPC:
            return new TopCAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case MAX:
            return new MaxAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case MIN:
            return new MinAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case AVG:
            return new AvgAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case MEDIAN:
            return new MedianAggegation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case CONCAT:
            return new ConcatAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case SHORTEST:
            return new ShortestAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case LONGEST:
            return new LongestAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        case NONE:
            return new NoneAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        default:
            if (type == null) {
                throw new IllegalArgumentException("Cannot create AggregationMethod of null type");
            } else {
                throw new AggregationNotImplementedException(type);
            }
        }
    }

    /**
     * Creates a new instance of AggregationMethod for aggregating a single conflicting quad.
     * @param aggregationSpec aggregation settings
     * @see #getSingleValueAggregation()
     * @return a new instance of AggregationMethod
     */
    protected ObjectAggregationMethod createSingleValueAggregation(AggregationSpec aggregationSpec) {
        return new SingleValueAggregation(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }
}
