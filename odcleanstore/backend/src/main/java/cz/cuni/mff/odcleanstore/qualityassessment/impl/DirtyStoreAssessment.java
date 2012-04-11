package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import virtuoso.jena.driver.VirtGraph;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

public class DirtyStoreAssessment extends CommonAssessment {
	
	@Override
	protected void loadGraph() {

		SparqlEndpoint dirty = context.getDirtyDatabaseEndpoint();
		
		this.graph = new VirtGraph(inputGraph.getGraphName(),
				dirty.getUri(),
				dirty.getUsername(),
				dirty.getPassword());
	}

	protected void loadMetadataGraph () {

		SparqlEndpoint dirty = context.getDirtyDatabaseEndpoint();
		
		this.metadataGraph = new VirtGraph(inputGraph.getMetadataGraphName(),
				dirty.getUri(),
				dirty.getUsername(),
				dirty.getPassword());
	}
	
	@Override
	protected void storeMetadataGraph() {
		//TODO: STORE METADATA INTO THE CORRECT DATABASE
		
	}
}
