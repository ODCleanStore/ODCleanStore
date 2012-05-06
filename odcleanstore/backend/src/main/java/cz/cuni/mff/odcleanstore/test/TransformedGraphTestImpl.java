package cz.cuni.mff.odcleanstore.test;

import java.util.Collection;
import java.util.UUID;

import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;

public class TransformedGraphTestImpl implements TransformedGraph {
	
	private String graphName;
	
	public TransformedGraphTestImpl(String graphName) {
		this.graphName = graphName;
	}

	@Override
	public String getGraphName() {
		return graphName;
	}

	@Override
	public String getGraphId() {
		return UUID.randomUUID().toString();
	}

	@Override
	public String getMetadataGraphName() {
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
