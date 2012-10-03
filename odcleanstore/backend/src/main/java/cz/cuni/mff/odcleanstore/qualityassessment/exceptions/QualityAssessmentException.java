package cz.cuni.mff.odcleanstore.qualityassessment.exceptions;

import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

public class QualityAssessmentException extends ODCleanStoreException {
	private static final long serialVersionUID = 1L;

	public QualityAssessmentException(Throwable throwable) {
		super(throwable);
	}

	public QualityAssessmentException(String message) {
		super(message);
	}
}
