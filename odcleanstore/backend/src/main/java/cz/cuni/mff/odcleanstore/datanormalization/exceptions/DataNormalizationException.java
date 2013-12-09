package cz.cuni.mff.odcleanstore.datanormalization.exceptions;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Exception specific to data normalization related transformations, rule querying or rule generation.
 *
 * @author Jakub Daniel
 *
 */
public class DataNormalizationException extends ODCleanStoreException {
    private static final long serialVersionUID = 1L;

    public DataNormalizationException(Throwable throwable) {
        super(throwable);
    }

    public DataNormalizationException(String message) {
        super(message);
    }
}
