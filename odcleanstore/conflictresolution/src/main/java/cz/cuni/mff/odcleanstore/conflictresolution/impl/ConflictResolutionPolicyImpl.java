/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Collections;
import java.util.Map;

import org.openrdf.model.URI;

import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;

/**
 * Basic implementation of {@link ConflictResolutionPolicy}.
 * @see ConflictResolutionPolicy
 * @author Jan Michelfeit
 */
public class ConflictResolutionPolicyImpl implements ConflictResolutionPolicy {
    private ResolutionStrategy defaultResolutionStrategy;
    private Map<URI, ResolutionStrategy> propertyResolutionStrategy;

    /**
     * Creates a new instance with default resolution behavior of the conflict resolver.
     */
    public ConflictResolutionPolicyImpl() {
        this(null, Collections.<URI, ResolutionStrategy>emptyMap());
    }

    /**
     * Creates a new instance with the given default and per-property resolution strategies.
     * @param defaultResolutionStrategy default conflict resolution strategy
     * @param propertyResolutionStrategy map of per-property conflict resolution strategies
     */
    public ConflictResolutionPolicyImpl(
            ResolutionStrategy defaultResolutionStrategy,
            Map<URI, ResolutionStrategy> propertyResolutionStrategy) {
        this.defaultResolutionStrategy = defaultResolutionStrategy;
        this.propertyResolutionStrategy = propertyResolutionStrategy;
    }

    @Override
    public ResolutionStrategy getDefaultResolutionStrategy() {
        return defaultResolutionStrategy;
    }

    /**
     * Sets default conflict resolution strategy.
     * @param defaultResolutionStrategy default resolution strategy
     */
    public void setDefaultResolutionStrategy(ResolutionStrategy defaultResolutionStrategy) {
        this.defaultResolutionStrategy = defaultResolutionStrategy;
    }

    @Override
    public Map<URI, ResolutionStrategy> getPropertyResolutionStrategies() {
        return propertyResolutionStrategy;
    }

    /**
     * Sets map of per-property resolution strategies.
     * @param propertyResolutionStrategy map of per-property conflict resolution strategies
     */
    public void setPropertyResolutionStrategy(Map<URI, ResolutionStrategy> propertyResolutionStrategy) {
        this.propertyResolutionStrategy = propertyResolutionStrategy;
    }
}
