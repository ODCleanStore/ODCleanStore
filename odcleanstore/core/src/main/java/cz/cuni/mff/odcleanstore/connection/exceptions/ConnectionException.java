/**
 *
 */
package cz.cuni.mff.odcleanstore.connection.exceptions;

/**
 * Exception thrown when a connection to the database cannot be established.
 * @author Jan Michelfeit
 */
public class ConnectionException extends DatabaseException {
	private static final long serialVersionUID = 1L;

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
