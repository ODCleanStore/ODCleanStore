package cz.cuni.mff.odcleanstore.conflictresolution.exceptions;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * Exception during Conflict Resolution.
 * @author Jan Michelfeit
 */
public class ConflictResolutionException extends ODCleanStoreException {
    /**
     * Creates a new exception.
     * @param cause cause
     */
    public ConflictResolutionException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     * @param cause cause
     */
    public ConflictResolutionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     */
    public ConflictResolutionException(String message) {
        super(message);
    }
}
