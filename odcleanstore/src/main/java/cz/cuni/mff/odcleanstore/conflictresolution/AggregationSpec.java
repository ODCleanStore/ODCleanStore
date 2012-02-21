package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Map;
import java.util.TreeMap;

/**
 * Encapsulates settings for aggregation during the conflict resolution process.
 * 
 * @author Jan Michelfeit
 */
public class AggregationSpec {
    /**
     * Type of strategy to use when an aggregation cannot be applied to a value.
     */
    private AggregationErrorStrategy errorStrategy = AggregationErrorStrategy.RETURN_ALL;
    
    /** 
     * Type of aggregation method for properties not included in #aggregationMethods.
     */
    private AggregationType defaultAggregation = AggregationType.ALL;
    
    /**
     * Map of property URI -> selected aggregation method.
     */
    private Map<String, AggregationType> propertyAggregations;
    
    /**
     * Create instance with default aggregation settings 
     * (default aggregation ALL, error strategy RETURN_ALL).
     */
    public AggregationSpec() {
        propertyAggregations = new TreeMap<String, AggregationType>();
    }
    
    /**
     * Create instance with all settings specified by parameters.
     * @param defaultAggregation aggregation method for properties not included
     *      in propertyAggregations; cannot be null
     * @param propertyAggregations aggregation method for properties 
     *      as a property URI -> aggregation type map; cannot be null
     * @param errorStrategy  strategy tu use when an aggregation cannote be
     *      applied to a value; cannot be null
     */
    public AggregationSpec(
            AggregationType defaultAggregation,
            Map<String, AggregationType> propertyAggregations,
            AggregationErrorStrategy errorStrategy) {
        
        setDefaultAggregation(defaultAggregation);
        setPropertyAggregations(propertyAggregations);
        setErrorStrategy(errorStrategy);
    }
    
    /**
     * Create instance with a default aggregation error strategy.
     * @param defaultAggregation aggregation method for properties not included
     *      in propertyAggregations
     * @param propertyAggregations aggregation method for properties 
     *      as a property URI -> aggregation type map
     */
    public AggregationSpec(
            AggregationType defaultAggregation,
            Map<String, AggregationType> propertyAggregations) {
        
        setDefaultAggregation(defaultAggregation);
        setPropertyAggregations(propertyAggregations);
    }
    
    /** 
     * Return aggregation error strategy. 
     * @return the aggregation error strategy
     */
    public final AggregationErrorStrategy getErrorStrategy() {
        return errorStrategy;
    }
    
    /**
     * Set aggregation error strategy.
     * @param errorStrategy the new aggregation error strategy; must not be null
     */
    public final void setErrorStrategy(AggregationErrorStrategy errorStrategy) {
        if (errorStrategy == null) {
            throw new IllegalArgumentException("Aggregation error strategy must be null");
        }
       this.errorStrategy = errorStrategy;
    }
    
    /** 
     * Return aggregation method for properties without an explicitly set method.
     * @return the default aggregation type
     */
    public final AggregationType getDefaultAggregation() {
        return defaultAggregation;
    }
    
    /** 
     * Set aggregation method for properties without an explicitly set method.
     * @param defaultAggregation the new default aggregation type; must not be null
     */
    public final void setDefaultAggregation(AggregationType defaultAggregation) {
        if (defaultAggregation == null) {
            throw new IllegalArgumentException("Aggregation type cannot be null");
        }
       this.defaultAggregation = defaultAggregation;
    }

    /**
     * Get aggregation methods explicitly set for properties. 
     * @return map of aggregation types for properties
     */
    public final Map<String, AggregationType> getPropertyAggregations() {
        return propertyAggregations;
    }

    /**
     * Set aggregation methods for properties.
     * @param propertyAggregations aggregation method for properties 
     *      as a property URI -> aggregation type map; must not be null
     */
    public final void setPropertyAggregations(Map<String, AggregationType> propertyAggregations) {
        if (propertyAggregations == null) {
            throw new IllegalArgumentException("Aggregation types for properties cannot be null");
        }
        this.propertyAggregations = propertyAggregations;
    }
    
    /** 
     * Return aggregation method for a selected property. 
     * @param propertyURI the URI of a property
     * @return the effective aggregation type for the property
     */
    public AggregationType propertyAggregationType(String propertyURI) {
        AggregationType result = this.propertyAggregations.get(propertyURI);
        if (result == null) {
            result = defaultAggregation;
        }
        return result;
    }
}
