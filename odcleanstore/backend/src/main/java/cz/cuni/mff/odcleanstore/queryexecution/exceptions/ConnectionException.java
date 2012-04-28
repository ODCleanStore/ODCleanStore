/**
 *
 */
package cz.cuni.mff.odcleanstore.queryexecution.exceptions;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * Exception thrown when a connection to the database cannot be established.
 * @author Jan Michelfeit
 */
public class ConnectionException extends ODCleanStoreException {
    /**
     * Creates a new exception.
     * @param cause cause
     */
    public ConnectionException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     * @param cause cause
     */
    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     */
    public ConnectionException(String message) {
        super(message);
    }
}
