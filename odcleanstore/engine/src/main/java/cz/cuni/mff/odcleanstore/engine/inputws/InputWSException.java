package cz.cuni.mff.odcleanstore.engine.inputws;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * A exception arising from InputWS code.
 * 
 * @author Petr Jerman
 */
public class InputWSException extends ODCleanStoreException {
	
	private static final long serialVersionUID = 1551176048380588306L;
	
	/**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
	InputWSException(String message) {
		super(message);
	}

	/**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
	InputWSException(Throwable cause) {
		super(cause);
	}
	
	/**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
	InputWSException(String message, Throwable cause) {
        super(message, cause);
    }
}
