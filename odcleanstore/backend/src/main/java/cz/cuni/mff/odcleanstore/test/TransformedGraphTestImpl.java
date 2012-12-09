package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Transformed graph mock object.
 * @author Tomas Soukup
 */
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
		return "demoData";
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
		Collection<String> result = new ArrayList<String>();
		result.add("http://opendata.cz/infrastructure/odcleanstore/internal/generatedLinks/demoData");
		return result;
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
