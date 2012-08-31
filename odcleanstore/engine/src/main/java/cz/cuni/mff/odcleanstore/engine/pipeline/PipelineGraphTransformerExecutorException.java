package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * A exception arising from PipelineGraphTransformerExecutor class.
 * 
 * @author Petr Jerman
 */
public class PipelineGraphTransformerExecutorException extends ODCleanStoreException {

	private static final long serialVersionUID = -7628445944871159626L;

	/**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
	PipelineGraphTransformerExecutorException(String message) {
		super(message);
	}
	
	/**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
	PipelineGraphTransformerExecutorException(String message, Throwable cause) {
        super(message, cause);
    }
}
