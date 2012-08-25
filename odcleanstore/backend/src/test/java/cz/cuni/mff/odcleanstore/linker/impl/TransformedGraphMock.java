package cz.cuni.mff.odcleanstore.linker.impl;

import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;

import java.util.Collection;

public class TransformedGraphMock implements TransformedGraph {

	private String graphName;

	public TransformedGraphMock(String graphName) {
		this.graphName = graphName;
	}

	@Override
	public String getGraphName() {
		return graphName;
	}

	@Override
	public String getGraphId() {
		return "graphUUID";
	}

	@Override
	public String getMetadataGraphName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public String getProvenanceMetadataGraphName() {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public Collection<String> getAttachedGraphNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAttachedGraph(String attachedGraphName)
			throws TransformedGraphException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteGraph() throws TransformedGraphException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDeleted() {
		// TODO Auto-generated method stub
		return false;
	}

}
