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
 * @author Jan Michelfeit
 */
public class ResolutionStrategyImpl implements ResolutionStrategy {
    private String resolutionFunctionName;
    private EnumCardinality cardinality;
    private EnumAggregationErrorStrategy aggregationErrorStrategy;
    private Map<String, String> params;

    public ResolutionStrategyImpl() {
        this(null, null, null, Collections.<String, String>emptyMap());
    }

    public ResolutionStrategyImpl(String resolutionFunctionName) {
        this(resolutionFunctionName, null, null, Collections.<String, String>emptyMap());
    }

    public ResolutionStrategyImpl(String resolutionFunctionName, EnumCardinality cardinality,
            EnumAggregationErrorStrategy aggregationErrorStrategy) {
        this(resolutionFunctionName, cardinality, aggregationErrorStrategy, Collections.<String, String>emptyMap());
    }

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

    public void setResolutionFunctionName(String resolutionFunctionName) {
        this.resolutionFunctionName = resolutionFunctionName;
    }

    public void setCardinality(EnumCardinality cardinality) {
        this.cardinality = cardinality;
    }

    public void setAggregationErrorStrategy(EnumAggregationErrorStrategy aggregationErrorStrategy) {
        this.aggregationErrorStrategy = aggregationErrorStrategy;
    }

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
