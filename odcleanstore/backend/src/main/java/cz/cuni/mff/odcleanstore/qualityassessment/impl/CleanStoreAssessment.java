package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

/**
 * Implementation of Clean Database specific steps of the
 * assessment process.
 */
public class CleanStoreAssessment extends CommonAssessment {

	@Override
	protected SparqlEndpoint getEndpoint() {
		return context.getCleanDatabaseEndpoint();
	}

	//TODO: update publishers score
	// drop old score, keep number of graphs, add new score
}
