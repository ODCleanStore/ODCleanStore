package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationType;

/**
 * Factory class for various quad aggregation methods.
 * 
 * @author Jan Michelfeit
 */
public class AggregationMethodFactory {
    /** Hide constructor for a utility class. */
    protected AggregationMethodFactory() {
    }

    /**
     * Create a new instance of AggregationMethod implementing the selected
     * type of aggregation.
     * @param type type of aggregation
     * @return an object implementing the selected aggregation method
     * @todo singletons? implement the factory as a registry??
     * @throws AggregationNotImplementedException thrown if there is no 
     *      AggregationMethod implementation for the selected aggregation type
     * @see AggregationType
     */
    public static AggregationMethod getAggregation(AggregationType type) 
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
                    throw new IllegalArgumentException(
                            "Cannot create AggregationMethod of null type");
                } else {
                    throw new AggregationNotImplementedException(type);
                }
        }
    }
    
    /**
     * Returns an instance of AggregationMethod for aggregating a single 
     * conflicting quad. 
     * Since the behavior of AggregationMethods when 
     * aggragating a single value is supposed to be the same, this instance
     * can be used in place of an arbitrary aggregation type on a single quad
     * and possibly provide a better performance than a specialized aggregation
     * method.
     * @return an instance of AggregationMethod 
     */
    public static AggregationMethod getSingleValueAggregation() {
        return new SingleValueAggregation();
    }
}
