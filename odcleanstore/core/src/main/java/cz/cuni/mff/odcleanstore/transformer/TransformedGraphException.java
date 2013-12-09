/**
 *
 */
package cz.cuni.mff.odcleanstore.transformer;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Exception thrown by TransformedGraphs during a named graph processing.
 * @author Petr Jerman
 */
public class TransformedGraphException extends ODCleanStoreException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
    public TransformedGraphException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public TransformedGraphException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    public TransformedGraphException(String message) {
        super(message);
    }
}
