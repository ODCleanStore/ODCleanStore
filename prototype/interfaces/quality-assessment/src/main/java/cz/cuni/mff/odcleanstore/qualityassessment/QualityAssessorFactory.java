package cz.cuni.mff.odcleanstore.qualityassessment;

import cz.cuni.mff.odcleanstore.qualityassessment.impl.*;

public class QualityAssessorFactory {
	public static QualityAssessor createAssessor () {
		return new QualityAssessorImpl();
	}
}
