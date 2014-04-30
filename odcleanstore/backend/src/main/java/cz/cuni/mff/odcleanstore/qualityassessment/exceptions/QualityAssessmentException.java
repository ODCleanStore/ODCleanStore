package cz.cuni.mff.odcleanstore.qualityassessment.exceptions;

import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;

/**
 * Exception specific to Quality Assessment transformations and rule querying and rule generation
 *
 * @author Jakub Daniel
 */
public class QualityAssessmentException extends ODCleanStoreException {
	private static final long serialVersionUID = 1L;

	public QualityAssessmentException(Throwable throwable) {
		super(throwable);
	}

	public QualityAssessmentException(String message) {
		super(message);
	}
}
