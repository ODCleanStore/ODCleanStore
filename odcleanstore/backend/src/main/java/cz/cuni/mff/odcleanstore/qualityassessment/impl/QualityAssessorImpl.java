package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.qualityassessment.*;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * The default quality assessor.
 *
 * Depending on the situation selects implementation of quality assessment
 * and delegates the work to that implementation.
 */
public class QualityAssessorImpl implements QualityAssessor {
	
	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {

		/**
		 * New graphs are always in the dirty database.
		 */
		try {
			new DirtyStoreAssessment().assessQuality(inputGraph, context);
		} catch (ODCleanStoreException e) {
			throw new TransformerException(e.getMessage());
		}
	}

	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		
		/**
		 * Existing (successfully processed graphs) are in the clean database.
		 */
		try {
			new CleanStoreAssessment().assessQuality(inputGraph, context);
		} catch (ODCleanStoreException e) {
			throw new TransformerException(e.getMessage());
		}
	}
	
	@Override
    public void shutdown() {
    }
}
