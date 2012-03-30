package cz.cuni.mff.odcleanstore.shared;

/**
 * A base class of all exceptions arising from ODCleanStore code.
 *
 * @author Jan Michelfeit
 */
public class ODCleanStoreException extends Exception {
    /**
     * Constructs a new exception with the specified cause.
     * @param cause the cause
     */
    public ODCleanStoreException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public ODCleanStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified message.
     * @param message the detail message
     */
    public ODCleanStoreException(String message) {
        super(message);
    }
}
