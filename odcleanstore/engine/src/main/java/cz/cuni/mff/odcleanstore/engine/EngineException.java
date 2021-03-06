package cz.cuni.mff.odcleanstore.engine;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * A exception arising from Engine code.
 * 
 * @author Petr Jerman
 */
public class EngineException extends ODCleanStoreException {

    private static final long serialVersionUID = 7255963918600426372L;

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    EngineException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the given cause.
     * @param cause the cause
     */
    public EngineException(Throwable cause) {
        super(cause);
    }
}
