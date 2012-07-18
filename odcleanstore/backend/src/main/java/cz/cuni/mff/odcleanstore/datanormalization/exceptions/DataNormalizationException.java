package cz.cuni.mff.odcleanstore.datanormalization.exceptions;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

public class DataNormalizationException extends ODCleanStoreException {
	public DataNormalizationException(Throwable throwable) {
		super(throwable);
	}

	public DataNormalizationException(String message) {
		super(message);
	}
}
