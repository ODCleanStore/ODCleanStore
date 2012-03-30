package cz.cuni.mff.odcleanstore.conflictresolution;

import cz.cuni.mff.odcleanstore.conflictresolution.impl.ConflictResolverImpl;

/**
 * Factory class for ConflictResolver instances.
 * 
 * @author Jan Michelfeit
 */
public class ConflictResolverFactory {
    /**
     * Return a new instance of ConflictResolver.
     * @param spec settings for the conflict resolution process
     * @return a ConflictResolver instance
     */
    public static ConflictResolver createResolver(ConflictResolverSpec spec) {
        return new ConflictResolverImpl(spec);
    }

    /** Hide constructor for a utility class. */
    protected ConflictResolverFactory() {
    }
}
