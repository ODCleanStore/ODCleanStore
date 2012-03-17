package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for various quad aggregation methods.
 *
 * @author Jan Michelfeit
 */
public class AggregationMethodFactory {
    /**
     * Registry of already created aggregation methods.
     */
    private Map<EnumAggregationType, AggregationMethod> methodRegistry
            = new HashMap<EnumAggregationType, AggregationMethod>();

    /**
     * Instance of a single value aggregation.
     */
    private AggregationMethod singleValueAggregation;

    /**
     * Creates an instance of this class.
     */
    public AggregationMethodFactory() {
        this.singleValueAggregation = createSingleValueAggregation();
    }

    /**
     * Returns an instance of AggregationMethod implementing the selected type of aggregation.
     * @param type type of aggregation
     * @return an object implementing the selected aggregation method
     * @throws AggregationNotImplementedException thrown if there is no
     *         AggregationMethod implementation for the selected aggregation type
     * @see EnumAggregationType
     */
    public AggregationMethod getAggregation(EnumAggregationType type)
            throws AggregationNotImplementedException {
        AggregationMethod result = methodRegistry.get(type);
        if (result == null) {
            result = createAggregation(type);
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
     * @return an instance of AggregationMethod
     */
    public AggregationMethod getSingleValueAggregation() {
        return singleValueAggregation;
    }

    /**
     * Create a new instance of AggregationMethod implementing the selected type of aggregation.
     * @see #getAggregation(EnumAggregationType)
     * @param type type of aggregation
     * @return an new object implementing the selected aggregation method
     * @throws AggregationNotImplementedException thrown if there is no
     *         AggregationMethod implementation for the selected aggregation type
     */
    protected static AggregationMethod createAggregation(EnumAggregationType type)
            throws AggregationNotImplementedException {
        switch (type) {
        case ANY:
            return new AnyAggregation();
        case ALL:
            return new AllAggregation();
        case BEST:
            return new BestAggregation();
        case LATEST:
            return new LatestAggregation();
        case TOPC:
            return new TopCAggregation();
        case MIN:
            return new MinAggregation();
        case MAX:
            return new MaxAggregation();
        case AVG:
            return new AvgAggegation();
        case MEDIAN:
            return new MedianAggegation();
        case CONCAT:
            return new ConcatAggegation();
        case SHORTEST:
            return new ShortestAggregation();
        case LONGEST:
            return new LongestAggregation();
        case NONE:
            return new NoneAggregation();
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
     * @see #getSingleValueAggregation()
     * @return a new instance of AggregationMethod
     */
    protected static AggregationMethod createSingleValueAggregation() {
        return new SingleValueAggregation();
    }
}
