package cz.cuni.mff.odcleanstore.engine.db.model;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Exception thrown when executing a query to relational database.
 * @see DbOdcsContextTransactional
 * @author Petr Jerman
 */
public class DbOdcsException extends ODCleanStoreException {

    private static final long serialVersionUID = 1162016892593354364L;

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    DbOdcsException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
    DbOdcsException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    DbOdcsException(String message, Throwable cause) {
        super(message, cause);
    }
}
