package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunction;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ResolutionFunctionNotRegisteredException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic implementation of {@link ResolutionFunctionRegistry}.
 * Resolution function implementations can be added by their identifier using
 * {@link #register(String, cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunction)} ()}.
 * This class is thread-safe.
 * @author Jan Michelfeit
 */
public class ResolutionFunctionRegistryImpl implements ResolutionFunctionRegistry {
    private final Map<String, ResolutionFunction> functions = new ConcurrentHashMap<String, ResolutionFunction>();

    @Override
    public ResolutionFunction get(String functionName) throws ResolutionFunctionNotRegisteredException {
        ResolutionFunction result = functions.get(functionName);
        if (result == null) {
            throw new ResolutionFunctionNotRegisteredException(functionName);
        }
        return result;
    }

    @Override
    public void register(String functionName, ResolutionFunction resolutionFunction) {
        functions.put(functionName, resolutionFunction);
    }
    
    @Override
    public Iterable<String> listRegisteredFunctions() {
        return functions.keySet();
    }
}
