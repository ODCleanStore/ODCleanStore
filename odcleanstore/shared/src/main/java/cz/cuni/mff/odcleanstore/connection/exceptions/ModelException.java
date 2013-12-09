package cz.cuni.mff.odcleanstore.connection.exceptions;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Error related to database model.
 * @author Jan Michelfeit
 */
public class ModelException extends ODCleanStoreException {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception.
     * @param cause cause
     */
    public ModelException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     * @param cause cause
     */
    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     */
    public ModelException(String message) {
        super(message);
    }
}
