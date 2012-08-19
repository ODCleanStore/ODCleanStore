package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * A exception arising from PipelineGraphManipulation class.
 * 
 * @author Petr Jerman
 */
public class PipelineGraphManipulatorException extends ODCleanStoreException {

	private static final long serialVersionUID = -7895488119460441311L;

	/**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
	PipelineGraphManipulatorException(String message) {
		super(message);
	}

	/**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
	PipelineGraphManipulatorException(Throwable cause) {
		super(cause);
	}
	
	/**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
	PipelineGraphManipulatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
