package cz.cuni.mff.odcleanstore.engine.db;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * Database transaction error.
 * @author Petr Jerman
 */
public class DbTransactionException extends ODCleanStoreException {

    private static final long serialVersionUID = -3936080838533324992L;

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    DbTransactionException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
    DbTransactionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    DbTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
