package cz.cuni.mff.odcleanstore.engine.pipeline;

/**
 * A exception arising from TransformedGraph class.
 * 
 * @author Petr Jerman
 */
public class TransformationContextRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 6749036631147973995L;

    /**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
    TransformationContextRuntimeException(String message) {
        super(message);
    }
}
