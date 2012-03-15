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
     * The multivalue setting for quality computation.
     * If set to true, differences with other conflicting values don't decrease the quality.
     */
    private boolean defaultMultivalue = false;

    /**
     * Map of property URI -> multivalue setting for the property.
     * @see #defaultMultivalue
     */
    private Map<String, Boolean> propertyMultivalue;

    /**
     * Create instance with default aggregation settings
     * (default aggregation ALL, error strategy RETURN_ALL).
     */
    public AggregationSpec() {
        propertyAggregations = new TreeMap<String, EnumAggregationType>();
        propertyMultivalue = new TreeMap<String, Boolean>();
    }

    /**
     * Create instance with all settings specified by parameters.
     * @param defaultAggregation aggregation method for properties not included
     *        in propertyAggregations; cannot be null
     * @param propertyAggregations aggregation method for properties
     *        as a property URI -> aggregation type map; cannot be null
     * @param errorStrategy strategy to use when an aggregation cannot be
     *        applied to a value; cannot be null
     * @param propertyMultivalue indicates whether decrease score of a value in presence
     *        of different conflicting values (false) or not (true) for each property;
     *        map of property URI -> multivalue setting for the property
     */
    public AggregationSpec(
            EnumAggregationType defaultAggregation,
            Map<String, EnumAggregationType> propertyAggregations,
            EnumAggregationErrorStrategy errorStrategy,
            Map<String, Boolean> propertyMultivalue) {

        setDefaultAggregation(defaultAggregation);
        setPropertyAggregations(propertyAggregations);
        setErrorStrategy(errorStrategy);
    }

    /**
     * Create instance with all settings specified by parameters.
     * @param defaultAggregation aggregation method for properties not included
     *        in propertyAggregations; cannot be null
     * @param propertyAggregations aggregation method for properties
     *        as a property URI -> aggregation type map; cannot be null
     * @param errorStrategy strategy to use when an aggregation cannot be
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
     * The default value can be overridden by {@link #setPropertyAggregations(Map)}
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
     * Get the default multivalue setting; if the multivalue setting is set to false,
     * differences with other conflicting values decrease quality estimate, otherwise they don't.
     * The default value can be overridden by {@link #setPropertyAggregations(Map)}.
     * @return the default multivalue setting
     */
    public final boolean getDefaultMultivalue() {
        return defaultMultivalue;
    }

    /**
     * Sets the default value of the multivalue setting.
     * @see #getDefaultMultivalue()
     * @param defaultMultivalue default value of the multivalue setting
     */
    public final void setDefaultMultivalue(boolean defaultMultivalue) {
        this.defaultMultivalue = defaultMultivalue;
    }


    /**
     * Returns explicit multivalue settings for predicates.
     * @see #getDefaultMultivalue()
     * @return map of property URI -> the multivalue setting for the property
     */
    public final Map<String, Boolean> getPropertyMultivalue() {
        return propertyMultivalue;
    }

    /**
     * Sets explicit multivalue settings per predicate.
     * @see #getDefaultMultivalue()
     * @param propertyMultivalue map of property URI -> the multivalue setting for the property
     */
    public final void setPropertyMultivalue(Map<String, Boolean> propertyMultivalue) {
        if (propertyMultivalue == null) {
            throw new IllegalArgumentException("Multivalue settings for properties cannot be null");
        }
        this.propertyMultivalue = propertyMultivalue;
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

    /**
     * Return the effective value of the multivalue attribute for a selected property.
     * @see #getDefaultMultivalue()
     * @param propertyURI the URI of a property
     * @return the effective multivalue setting for the property
     */
    public boolean isPropertyMultivalue(String propertyURI) {
        Boolean result = this.propertyMultivalue.get(propertyURI);
        if (result == null) {
            result = defaultMultivalue;
        }
        return result;
    }
}
