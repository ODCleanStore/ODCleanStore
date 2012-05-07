package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * Exception thrown when a query is invalid.
 * @author Jan Michelfeit
 */
public class QueryFormatException extends ODCleanStoreException {
    /**
     * Creates a new exception.
     * @param cause cause
     */
    public QueryFormatException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     * @param cause cause
     */
    public QueryFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     */
    public QueryFormatException(String message) {
        super(message);
    }
}
