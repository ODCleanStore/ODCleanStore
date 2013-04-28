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
 * @author Jan Michelfeit
 */
public class ConflictResolutionPolicyImpl implements ConflictResolutionPolicy {
    private ResolutionStrategy defaultResolutionStrategy;
    private Map<URI, ResolutionStrategy> propertyResolutionStrategy;

    public ConflictResolutionPolicyImpl() {
        this(null, Collections.<URI, ResolutionStrategy>emptyMap());
    }

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

    public void setDefaultResolutionStrategy(ResolutionStrategy defaultResolutionStrategy) {
        this.defaultResolutionStrategy = defaultResolutionStrategy;
    }

    @Override
    public Map<URI, ResolutionStrategy> getPropertyResolutionStrategies() {
        return propertyResolutionStrategy;
    }

    public void setPropertyResolutionStrategy(Map<URI, ResolutionStrategy> propertyResolutionStrategy) {
        this.propertyResolutionStrategy = propertyResolutionStrategy;
    }
}
