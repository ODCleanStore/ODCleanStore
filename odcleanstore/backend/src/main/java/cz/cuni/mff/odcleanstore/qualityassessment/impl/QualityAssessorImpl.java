package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.qualityassessment.*;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public class QualityAssessorImpl implements QualityAssessor {
	
	@Override
	public void transformNewGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {

		try {
			new DirtyStoreAssessment().assessQuality(inputGraph, context);
		} catch (ODCleanStoreException e) {
			throw new TransformerException(e.getMessage());
		}
	}

	@Override
	public void transformExistingGraph(TransformedGraph inputGraph,
			TransformationContext context) throws TransformerException {
		
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
