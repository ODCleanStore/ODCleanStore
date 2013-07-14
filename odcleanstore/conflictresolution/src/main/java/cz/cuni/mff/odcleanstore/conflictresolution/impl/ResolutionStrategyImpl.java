/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Collections;
import java.util.Map;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;

/**
 * Basic implementation of {@link ResolutionStrategy}.
 * @author Jan Michelfeit
 */
public class ResolutionStrategyImpl implements ResolutionStrategy {
    private String resolutionFunctionName;
    private EnumCardinality cardinality;
    private EnumAggregationErrorStrategy aggregationErrorStrategy;
    private Map<String, String> params;

    /**
     * Creates a new instance with no settings specified
     * ({@link cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver} default will be used).
     */
    public ResolutionStrategyImpl() {
        this(null, null, null, Collections.<String, String>emptyMap());
    }

    /**
     * Creates a new instance with the specified settings.
     * @param resolutionFunctionName name of the conflict resolution function to be used
     */
    public ResolutionStrategyImpl(String resolutionFunctionName) {
        this(resolutionFunctionName, null, null, Collections.<String, String>emptyMap());
    }

    /**
     * Creates a new instance with the specified settings.
     * @param resolutionFunctionName name of the conflict resolution function to be used
     * @param cardinality cardinality of the property appearing in the conflict cluster
     * @param aggregationErrorStrategy type of strategy to use when an aggregation by resolution
     *        function cannot be applied to a value
     */
    public ResolutionStrategyImpl(String resolutionFunctionName, EnumCardinality cardinality,
            EnumAggregationErrorStrategy aggregationErrorStrategy) {
        this(resolutionFunctionName, cardinality, aggregationErrorStrategy, Collections.<String, String>emptyMap());
    }

    /**
     * Creates a new instance with the specified settings.
     * @param resolutionFunctionName name of the conflict resolution function to be used
     * @param cardinality cardinality of the property appearing in the conflict cluster
     * @param aggregationErrorStrategy type of strategy to use when an aggregation by resolution
     *        function cannot be applied to a value
     * @param params additional parameters for the used conflict resolution function.
     */
    public ResolutionStrategyImpl(String resolutionFunctionName, EnumCardinality cardinality,
            EnumAggregationErrorStrategy aggregationErrorStrategy, Map<String, String> params) {
        this.resolutionFunctionName = resolutionFunctionName;
        this.cardinality = cardinality;
        this.aggregationErrorStrategy = aggregationErrorStrategy;
        setParams(params);
    }

    @Override
    public String getResolutionFunctionName() {
        return resolutionFunctionName;
    }

    @Override
    public EnumCardinality getCardinality() {
        return cardinality;
    }

    @Override
    public EnumAggregationErrorStrategy getAggregationErrorStrategy() {
        return aggregationErrorStrategy;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    /**
     * Sets value for {@link #getResolutionFunctionName()}.
     * @param resolutionFunctionName resolution function name
     */
    public void setResolutionFunctionName(String resolutionFunctionName) {
        this.resolutionFunctionName = resolutionFunctionName;
    }

    /**
     * Sets value for {@link #getCardinality()}.
     * @param cardinality property cardinality
     */
    public void setCardinality(EnumCardinality cardinality) {
        this.cardinality = cardinality;
    }

    /**
     * Sets value for {@link #getAggregationErrorStrategy()}.
     * @param aggregationErrorStrategy aggregation error strategy
     */
    public void setAggregationErrorStrategy(EnumAggregationErrorStrategy aggregationErrorStrategy) {
        this.aggregationErrorStrategy = aggregationErrorStrategy;
    }

    /**
     * Sets value for {@link #getParams()}.
     * @param params resolution function parameters
     */
    public void setParams(Map<String, String> params) {
        this.params = Collections.unmodifiableMap(params);
    }

    @Override
    public String toString() {
        return "(resolutionFunction=" + resolutionFunctionName
                + "; cardinality=" + cardinality
                + "; errorStrategy=" + aggregationErrorStrategy
                + "; params=" + params
                + ")";
    }
}
