package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * A exception arising from Pipeline code.
 * 
 * @author Petr Jerman
 */
public class PipelineException extends ODCleanStoreException {

	private static final long serialVersionUID = 1551176048380588306L;

	/**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
	PipelineException(String message) {
		super(message);
	}

	/**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
	PipelineException(Throwable cause) {
		super(cause);
	}
	
	/**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
	PipelineException(String message, Throwable cause) {
        super(message, cause);
    }
}
