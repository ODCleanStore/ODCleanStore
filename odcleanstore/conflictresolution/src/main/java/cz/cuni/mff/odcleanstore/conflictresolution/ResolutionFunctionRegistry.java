package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.HashMap;
import java.util.Map;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ResolutionFunctionNotRegisteredException;


/**
 * @author Jan Michelfeit
 */
public class ResolutionFunctionRegistry { // TODO
    private static final Map<String, ResolutionFunction> functions = new HashMap<String, ResolutionFunction>();
    
    public static ResolutionFunction get(String functionName) throws ResolutionFunctionNotRegisteredException {
        ResolutionFunction result = functions.get(functionName);
        if (result == null) {
            throw new ResolutionFunctionNotRegisteredException(functionName); // TODO: unchecked?
        }
        return result;
    }
    
    public static void register(String functionName, ResolutionFunction resolutionFunction) {
        functions.put(functionName, resolutionFunction);
    }
    
//    public void initDefaultFunctions(ConfidenceCalculator qualityCalculator) {
//        
//    }
}
