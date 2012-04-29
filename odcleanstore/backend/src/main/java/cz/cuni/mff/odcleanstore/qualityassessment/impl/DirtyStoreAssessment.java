package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

public class DirtyStoreAssessment extends CommonAssessment {

	@Override
	protected SparqlEndpoint getEndpoint() {
		return context.getDirtyDatabaseEndpoint();
	}
}
