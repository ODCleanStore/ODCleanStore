package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;

/**
 * Implementation of Dirty Database specific steps of the
 * assessment process.
 */
public class DirtyStoreAssessment extends CommonAssessment {

	@Override
	protected ConnectionCredentials getEndpoint() {
		return context.getDirtyDatabaseCredentials();
	}

        //TODO: update publishers score
	// add score to total, increase number of graphs by one
}
