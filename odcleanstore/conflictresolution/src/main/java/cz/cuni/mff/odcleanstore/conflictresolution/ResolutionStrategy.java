/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Map;

/**
 * Concrete conflict resolution settings for a conflict cluster (set of quads resolved together).
 * function to be used.
 * @author Jan Michelfeit
 */
public interface ResolutionStrategy {
    /**
     * Returns name of the conflict resolution function to be used.
     * @see ResolutionFunctionRegistry
     * @return an identifier of a conflict resolution function
     */
    String getResolutionFunctionName();

    /**
     * Cardinality of the property appearing in the conflict cluster.
     * @see EnumCardinality
     * @return cardinality of the conflict cluster's property
     */
    EnumCardinality getCardinality();

    /**
     * Returns type of strategy to use when an aggregation by resolution function cannot
     * be applied to a value.
     * @see EnumAggregationErrorStrategy
     * @return type of strategy to use when an aggregation by resolution function cannot
     *         be applied to a value
     */
    EnumAggregationErrorStrategy getAggregationErrorStrategy();

    /**
     * Additional parameters for the used conflict resolution function.
     * @return map of parameters as key -> value
     */
    Map<String, String> getParams();
}
