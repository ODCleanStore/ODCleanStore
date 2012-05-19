package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;

/**
 * Implementation of Clean Database specific steps of the
 * assessment process.
 */
public class CleanStoreAssessment extends CommonAssessment {

	@Override
	protected ConnectionCredentials getEndpoint() {
		return context.getCleanDatabaseCredentials();
	}

	//TODO: update publishers score
	// drop old score, keep number of graphs, add new score
}
