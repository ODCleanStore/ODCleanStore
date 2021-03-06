package cz.cuni.mff.odcleanstore.connection.exceptions;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Exception thrown when a database error occurs.
 * @author Jan Michelfeit
 */
public abstract class DatabaseException extends ODCleanStoreException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception.
     * @param cause cause
     */
    public DatabaseException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     * @param cause cause
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     */
    public DatabaseException(String message) {
        super(message);
    }
}
