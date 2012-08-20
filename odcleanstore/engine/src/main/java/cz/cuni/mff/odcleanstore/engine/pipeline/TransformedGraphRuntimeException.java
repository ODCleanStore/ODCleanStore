package cz.cuni.mff.odcleanstore.engine.pipeline;

/**
 * A exception arising from TransformedGraph class.
 * 
 * @author Petr Jerman
 */
public class TransformedGraphRuntimeException extends RuntimeException {
	
	private static final long serialVersionUID = 6613604269931579592L;

	/**
     * Constructs a new exception with the given message.
     * @param message the detail message
     */
	TransformedGraphRuntimeException(String message) {
		super(message);
	}
}
