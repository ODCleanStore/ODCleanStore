package cz.cuni.mff.odcleanstore.conflictresolution;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ResolutionFunctionNotRegisteredException;

/**
 * Registry of conflict resolution function implementations.
 * @author Jan Michelfeit
 */
public interface ResolutionFunctionRegistry {
    /**
     * Returns an implementation of a resolution function of the given name.
     * If no function of such name has been registered, throws an exception.
     * @param functionName name (string identifier) of the resolution function
     * @return implementation of the resolution function of the given name
     * @throws ResolutionFunctionNotRegisteredException no function of the given name is registered
     */
    ResolutionFunction get(String functionName) throws ResolutionFunctionNotRegisteredException;

    /**
     * Registers a conflict resolution function implementation with a given name.
     * Registering a function with the same name twice overrides the implementation of the function with
     * this name.
     * The function implementations should support being reused multiple times
     * (i.e. the resolved() method can be called repeatedly).
     * @param functionName name of the resolution function
     * @param resolutionFunction implementation of the resolution function
     */
    void register(String functionName, ResolutionFunction resolutionFunction);
    
    /**
     * Lists conflict resolution functions registered with {@link #register(String, ResolutionFunction)}.
     * @return list of resolution function names
     */
    Iterable<String> listRegisteredFunctions();
}
