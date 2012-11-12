/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.inputws;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * @author Petr Jerman
 *
 */
public class InputGraphStatusException extends ODCleanStoreException {

	private static final long serialVersionUID = -1948817020230014320L;

	private InputWSErrorEnumeration id;
	
	public InputWSErrorEnumeration getId() {
		return id;
	}

	public InputGraphStatusException(String message, InputWSErrorEnumeration id) {
		super(message);
		this.id = id;
	}

	public InputGraphStatusException(String message, Throwable cause) {
		super(message, cause);
		this.id = InputWSErrorEnumeration.FATAL_ERROR;
	}
}
