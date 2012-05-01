/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * @author jermanp
 * 
 */
public class EngineException extends ODCleanStoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	EngineException(String message) {
		super(message);
	}

	public EngineException(Throwable cause) {
		super(cause);
	}
}
