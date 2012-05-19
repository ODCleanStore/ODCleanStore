package cz.cuni.mff.odcleanstore.conflictresolution.exceptions;


/**
 * Exception to throw when an unexpected triple is used.
 *
 * @author Jan Michelfeit
 */
public class UnexpectedTripleException extends ConflictResolutionException {
    /**
     * Constructs a new exception with the specified message.
     * @param message the detail message
     */
    public UnexpectedTripleException(String message) {
        super(message);
    }
}
