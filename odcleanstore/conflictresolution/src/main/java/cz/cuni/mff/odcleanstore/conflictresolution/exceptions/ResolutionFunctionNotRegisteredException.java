package cz.cuni.mff.odcleanstore.conflictresolution.exceptions;

/**
 * Exception to throw when an attempt to create an AggregationMethod of
 * an unknown type is made.
 * @see _EnumAggregationType
 *
 * @author Jan Michelfeit
 */
public class ResolutionFunctionNotRegisteredException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final String resolutionFunctionName;
    
    public ResolutionFunctionNotRegisteredException(String resolutionFunctionName) {
        super("Resolution function identified by '" + resolutionFunctionName + "' is not registered");
        this.resolutionFunctionName = resolutionFunctionName;
    }
    
    public String getResolutionFunctionName() {
        return resolutionFunctionName;
    }
}
