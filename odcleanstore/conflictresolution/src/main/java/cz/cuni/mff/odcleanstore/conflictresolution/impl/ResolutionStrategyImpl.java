/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumCardinality;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import org.openrdf.model.URI;

import java.util.Collections;
import java.util.Map;

/**
 * Basic implementation of {@link ResolutionStrategy}.
 * @author Jan Michelfeit
 */
public class ResolutionStrategyImpl implements ResolutionStrategy {
    private String resolutionFunctionName;
    private EnumCardinality cardinality;
    private EnumAggregationErrorStrategy aggregationErrorStrategy;
    private Map<String, String> params;
    private URI dependsOn = null;

    /**
     * Creates a new instance with no settings specified
     * ({@link cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolver} default will be used).
     */
    public ResolutionStrategyImpl() {
        this(null, null, null, Collections.<String, String>emptyMap(), null);
    }

    /**
     * Creates a new instance with the specified settings.
     * @param resolutionFunctionName name of the conflict resolution function to be used
     */
    public ResolutionStrategyImpl(String resolutionFunctionName) {
        this(resolutionFunctionName, null, null, Collections.<String, String>emptyMap(), null);
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
        this(resolutionFunctionName, cardinality, aggregationErrorStrategy, Collections.<String, String>emptyMap(), null);
    }

    /**
     * Creates a new instance with the specified settings.
     * @param resolutionFunctionName name of the conflict resolution function to be used
     * @param cardinality cardinality of the property appearing in the conflict cluster
     * @param aggregationErrorStrategy type of strategy to use when an aggregation by resolution
 *        function cannot be applied to a value
     * @param params additional parameters for the used conflict resolution function.
     * @param dependsOn property on which we depend
     */
    public ResolutionStrategyImpl(String resolutionFunctionName, EnumCardinality cardinality,
            EnumAggregationErrorStrategy aggregationErrorStrategy, Map<String, String> params, URI dependsOn) {
        this.resolutionFunctionName = resolutionFunctionName;
        this.cardinality = cardinality;
        this.aggregationErrorStrategy = aggregationErrorStrategy;
        this.dependsOn = dependsOn;
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

    @Override
    public URI getDependsOn() {
        return dependsOn;
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
     * Sets value for {@link #getDependsOn()}.
     * @param dependsOn property on which we depend
     */
    public void setDependsOn(URI dependsOn) {
        this.dependsOn = dependsOn;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResolutionStrategyImpl that = (ResolutionStrategyImpl) o;

        if (aggregationErrorStrategy != that.aggregationErrorStrategy) {
            return false;
        }
        if (cardinality != that.cardinality) {
            return false;
        }
        if (dependsOn != null ? !dependsOn.equals(that.dependsOn) : that.dependsOn != null) {
            return false;
        }
        if (params != null ? !params.equals(that.params) : that.params != null) {
            return false;
        }
        if (resolutionFunctionName != null ? !resolutionFunctionName.equals(that.resolutionFunctionName) : that.resolutionFunctionName != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = resolutionFunctionName != null ? resolutionFunctionName.hashCode() : 0;
        result = 31 * result + (cardinality != null ? cardinality.hashCode() : 0);
        result = 31 * result + (aggregationErrorStrategy != null ? aggregationErrorStrategy.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (dependsOn != null ? dependsOn.hashCode() : 0);
        return result;
    }
}
