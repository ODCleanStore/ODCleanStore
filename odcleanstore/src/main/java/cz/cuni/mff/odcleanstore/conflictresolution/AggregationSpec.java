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
    private EnumAggregationErrorStrategy errorStrategy = EnumAggregationErrorStrategy.RETURN_ALL;

    /**
     * Type of aggregation method for properties not included in #aggregationMethods.
     */
    private EnumAggregationType defaultAggregation = EnumAggregationType.ALL;

    /**
     * Map of property URI -> selected aggregation method.
     */
    private Map<String, EnumAggregationType> propertyAggregations;

    /**
     * Create instance with default aggregation settings
     * (default aggregation ALL, error strategy RETURN_ALL).
     */
    public AggregationSpec() {
        propertyAggregations = new TreeMap<String, EnumAggregationType>();
    }

    /**
     * Create instance with all settings specified by parameters.
     * @param defaultAggregation aggregation method for properties not included
     *        in propertyAggregations; cannot be null
     * @param propertyAggregations aggregation method for properties
     *        as a property URI -> aggregation type map; cannot be null
     * @param errorStrategy strategy tu use when an aggregation cannote be
     *        applied to a value; cannot be null
     */
    public AggregationSpec(
            EnumAggregationType defaultAggregation,
            Map<String, EnumAggregationType> propertyAggregations,
            EnumAggregationErrorStrategy errorStrategy) {

        setDefaultAggregation(defaultAggregation);
        setPropertyAggregations(propertyAggregations);
        setErrorStrategy(errorStrategy);
    }

    /**
     * Create instance with a default aggregation error strategy.
     * @param defaultAggregation aggregation method for properties not included
     *        in propertyAggregations
     * @param propertyAggregations aggregation method for properties
     *        as a property URI -> aggregation type map
     */
    public AggregationSpec(
            EnumAggregationType defaultAggregation,
            Map<String, EnumAggregationType> propertyAggregations) {

        setDefaultAggregation(defaultAggregation);
        setPropertyAggregations(propertyAggregations);
    }

    /**
     * Return aggregation error strategy.
     * @return the aggregation error strategy
     */
    public final EnumAggregationErrorStrategy getErrorStrategy() {
        return errorStrategy;
    }

    /**
     * Set aggregation error strategy.
     * @param errorStrategy the new aggregation error strategy; must not be null
     */
    public final void setErrorStrategy(EnumAggregationErrorStrategy errorStrategy) {
        if (errorStrategy == null) {
            throw new IllegalArgumentException("Aggregation error strategy must be null");
        }
        this.errorStrategy = errorStrategy;
    }

    /**
     * Return aggregation method for properties without an explicitly set method.
     * @return the default aggregation type
     */
    public final EnumAggregationType getDefaultAggregation() {
        return defaultAggregation;
    }

    /**
     * Set aggregation method for properties without an explicitly set method.
     * @param defaultAggregation the new default aggregation type; must not be null
     */
    public final void setDefaultAggregation(EnumAggregationType defaultAggregation) {
        if (defaultAggregation == null) {
            throw new IllegalArgumentException("Aggregation type cannot be null");
        }
        this.defaultAggregation = defaultAggregation;
    }

    /**
     * Get aggregation methods explicitly set for properties.
     * @return map of aggregation types for properties
     */
    public final Map<String, EnumAggregationType> getPropertyAggregations() {
        return propertyAggregations;
    }

    /**
     * Set aggregation methods for properties.
     * @param propertyAggregations aggregation method for properties
     *        as a property URI -> aggregation type map; must not be null
     */
    public final void setPropertyAggregations(
            Map<String, EnumAggregationType> propertyAggregations) {

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
    public EnumAggregationType propertyAggregationType(String propertyURI) {
        EnumAggregationType result = this.propertyAggregations.get(propertyURI);
        if (result == null) {
            result = defaultAggregation;
        }
        return result;
    }
}
