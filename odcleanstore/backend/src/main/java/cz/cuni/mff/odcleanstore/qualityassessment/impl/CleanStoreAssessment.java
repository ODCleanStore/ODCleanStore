package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

public class CleanStoreAssessment extends CommonAssessment {

	@Override
	protected SparqlEndpoint getEndpoint() {
		return context.getCleanDatabaseEndpoint();
	}
}
