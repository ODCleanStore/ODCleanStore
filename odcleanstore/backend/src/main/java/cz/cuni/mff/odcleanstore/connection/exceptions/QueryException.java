/**
 *
 */
package cz.cuni.mff.odcleanstore.connection.exceptions;

/**
 * Exception thrown when a query over the database fails.
 * @author Jan Michelfeit
 */
public class QueryException extends DatabaseException {
    /**
     * Creates a new exception.
     * @param cause cause
     */
    public QueryException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     * @param cause cause
     */
    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     * @param message message
     */
    public QueryException(String message) {
        super(message);
    }
}
