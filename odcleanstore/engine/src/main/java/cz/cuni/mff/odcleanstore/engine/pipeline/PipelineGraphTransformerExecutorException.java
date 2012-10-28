package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.engine.db.model.PipelineCommand;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * A exception arising from PipelineGraphTransformerExecutor class.
 * 
 * @author Petr Jerman
 */
public class PipelineGraphTransformerExecutorException extends ODCleanStoreException {

    private static final long serialVersionUID = -7628445944871159626L;
    
    private PipelineCommand command;
    
    
    /**
     * @return Command which caused exception, may be null
     */
    PipelineCommand getCommand() {
        return command;
    }

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    PipelineGraphTransformerExecutorException(String message, PipelineCommand command) {
        super(message);
        this.command = command;
    }
    
    /**
     * Constructs a new exception with the given message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    PipelineGraphTransformerExecutorException(String message, PipelineCommand command, Throwable cause) {
        super(message, cause);
        this.command = command;
    }
}
