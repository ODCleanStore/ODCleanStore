package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * @author jermanp
 * 
 */
public class PipelineException extends ODCleanStoreException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	PipelineException(String message) {
		super(message);
	}

	PipelineException(Throwable cause) {
		super(cause);
	}
}
