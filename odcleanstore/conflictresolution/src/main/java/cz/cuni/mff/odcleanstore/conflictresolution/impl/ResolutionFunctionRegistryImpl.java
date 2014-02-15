package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunction;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ResolutionFunctionNotRegisteredException;

/**
 * Basic implementation of {@link ResolutionFunctionRegistry}.
 * Resolution function implementations can be added by their identifier using {@link #register()}.
 * @author Jan Michelfeit
 */
public class ResolutionFunctionRegistryImpl implements ResolutionFunctionRegistry {
    private final Map<String, ResolutionFunction> functions = new HashMap<String, ResolutionFunction>();

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
