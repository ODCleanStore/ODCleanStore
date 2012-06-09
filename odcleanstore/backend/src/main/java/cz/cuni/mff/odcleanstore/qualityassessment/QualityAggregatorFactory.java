package cz.cuni.mff.odcleanstore.qualityassessment;

import cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAggregatorImpl;

public class QualityAggregatorFactory {
	public static QualityAggregator createAggregator () {
		return new QualityAggregatorImpl();
	}
}
