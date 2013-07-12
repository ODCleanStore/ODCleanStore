package cz.cuni.mff.odcleanstore.conflictresolution.exceptions;

/**
 * Exception to throw when an attempt to create an conflict resolution function 
 * without a registered implementation is made.
 * @see cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry
 * @author Jan Michelfeit
 */
public class ResolutionFunctionNotRegisteredException extends ConflictResolutionException {
    private static final long serialVersionUID = 1L;
    
    private final String resolutionFunctionName;

    /**
     * @param resolutionFunctionName name of the resolution function for which an implementation cannot be found
     */
    public ResolutionFunctionNotRegisteredException(String resolutionFunctionName) {
        super("Resolution function identified by '" + resolutionFunctionName + "' is not registered");
        this.resolutionFunctionName = resolutionFunctionName;
    }
    
    /** 
     * Returns name of the resolution function for which an implementation cannot be found.
     * @return name of the resolution function for which an implementation cannot be found
     */
    public String getResolutionFunctionName() {
        return resolutionFunctionName;
    }
}
