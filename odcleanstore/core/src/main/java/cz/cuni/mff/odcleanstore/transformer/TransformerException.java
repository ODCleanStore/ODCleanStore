/**
 *
 */
package cz.cuni.mff.odcleanstore.transformer;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Exception thrown by Transformers during a named graph processing.
 * @author Jan Michelfeit
 */
public class TransformerException extends ODCleanStoreException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
    public TransformerException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public TransformerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    public TransformerException(String message) {
        super(message);
    }
}
