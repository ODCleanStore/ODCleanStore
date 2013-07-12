package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Map;

import org.openrdf.model.URI;

/**
 * Encapsulates default and per-property conflict resolution strategies.
 * @see ResolutionStrategy
 * @author Jan Michelfeit
 */
public interface ConflictResolutionPolicy {
    /**
     * Returns default conflict resolution strategy.
     * @return default conflict resolution strategy
     */
    ResolutionStrategy getDefaultResolutionStrategy();

    /**
     * Map of per-property conflict resolution strategies as
     * property URI->resolution strategy.
     * @return map of per-property conflict resolution strategies
     */
    Map<URI, ResolutionStrategy> getPropertyResolutionStrategies();
}

