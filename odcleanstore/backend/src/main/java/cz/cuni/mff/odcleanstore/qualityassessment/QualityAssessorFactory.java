package cz.cuni.mff.odcleanstore.qualityassessment;

import cz.cuni.mff.odcleanstore.qualityassessment.impl.*;

/**
 * Factory class for QualityAssessor instances.
 *
 * @author Jakub Daniel
 */
public class QualityAssessorFactory {

	/**
	 * Return a new instance of default QualityAssessor
	 * implementation.
	 *
	 * @return a QualityAssessor instance
	 */
	public static QualityAssessor createAssessor () {
		return new QualityAssessorImpl();
	}
}
