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
	 * @param groupId The ID of the rule group that should be used to assess quality by the new QA instance.
	 *
	 * @return a QualityAssessor instance
	 */
	public static QualityAssessor createAssessor (Integer groupId) {
		return new QualityAssessorImpl(groupId);
	}
}
