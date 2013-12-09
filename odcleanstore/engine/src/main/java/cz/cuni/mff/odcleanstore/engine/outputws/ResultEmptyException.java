package cz.cuni.mff.odcleanstore.engine.outputws;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * A exception arising from QueryExecutorResourceBase and his child class ( all is one band :)).
 * 
 * @author Petr Jerman
 */
public class ResultEmptyException extends ODCleanStoreException {

    private static final long serialVersionUID = -6884923392473006245L;

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    ResultEmptyException(String message) {
        super(message);
    }
}
