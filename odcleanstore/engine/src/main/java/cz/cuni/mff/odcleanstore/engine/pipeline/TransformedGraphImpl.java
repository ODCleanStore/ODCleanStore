package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;

/**
 *  @author Petr Jerman
 */
public final class TransformedGraphImpl implements TransformedGraph {

	public static final String NOT_WORKING_TRANSFORMER = "Operation is permitted only for working transformer";

	private static class InputGraph {
		public WorkingInputGraphStatus workingInputGraphStatus;
		public int dbKeyId;
		public String uuid;
		public boolean isDeleted;
		public int totalAttachedGraphsCount;
	};

	private InputGraph _inputGraph;
	private TransformedGraphImpl _prevTransformedGraphImpl;
	private ArrayList<String> _attachedGraphNames;

	TransformedGraphImpl(WorkingInputGraphStatus workingInputGraphStatus,int dbKeyId, String uuid) {
		if (workingInputGraphStatus == null || dbKeyId == 0) {
			throw new IllegalArgumentException();
		}
		
		_inputGraph = new InputGraph();
		_inputGraph.workingInputGraphStatus = workingInputGraphStatus;
		_inputGraph.dbKeyId = dbKeyId;
		_inputGraph.uuid = uuid;
		_inputGraph.isDeleted = false;
		_inputGraph.totalAttachedGraphsCount = 0;

		_prevTransformedGraphImpl = null;
		_attachedGraphNames = new ArrayList<String>();
	}

	TransformedGraphImpl(TransformedGraphImpl prevTransformedGraphImpl) {
		if (prevTransformedGraphImpl == null) {
			throw new IllegalArgumentException();
		}

		_inputGraph = prevTransformedGraphImpl._inputGraph;
		_prevTransformedGraphImpl = prevTransformedGraphImpl;
		_attachedGraphNames = new ArrayList<String>();
	}
	
	@Override
	public String getGraphName() {
		return ConfigLoader.getConfig().getBackendGroup().getDataGraphURIPrefix() + getGraphId();
	}
	
	int getGraphDbKeyId() {
		return _inputGraph.dbKeyId;
	}

	@Override
	public String getGraphId() {
		return _inputGraph.uuid;
	}

	@Override
	public String getMetadataGraphName() {
		return ConfigLoader.getConfig().getBackendGroup().getMetadataGraphURIPrefix() + getGraphId();
	}

	@Override
	public Collection<String> getAttachedGraphNames() {
		synchronized (_inputGraph) {
			ArrayList<String> al = new ArrayList<String>(_inputGraph.totalAttachedGraphsCount);
			getAttachedGraphNamesInternal(al);
			return al;
		}
	}

	private void getAttachedGraphNamesInternal(ArrayList<String> al) {
		if (_prevTransformedGraphImpl != null) {
			_prevTransformedGraphImpl.getAttachedGraphNamesInternal(al);
		}
		al.addAll(_attachedGraphNames);
	}
	
	private boolean containsAttachedGraphName(String graphName) {
		if (_prevTransformedGraphImpl != null) {
			if(_prevTransformedGraphImpl.containsAttachedGraphName(graphName)) {
				return true;
			}
		}
		return _attachedGraphNames.contains(graphName);
	}

	@Override
	public void addAttachedGraph(String attachedGraphName) throws TransformedGraphException {
		synchronized (_inputGraph) {
			try {
				if (containsAttachedGraphName(attachedGraphName)) {
					return;
				}
				_inputGraph.workingInputGraphStatus.addAttachedGraphName(this, attachedGraphName);
				_attachedGraphNames.add(attachedGraphName);
				_inputGraph.totalAttachedGraphsCount++;
			} catch (WorkingInputGraphStatus.NotWorkingTransformerException e) {
				throw new TransformedGraphException(NOT_WORKING_TRANSFORMER);
			} catch (Exception e) {
				throw new TransformedGraphException(e);
			}
		}
	}

	@Override
	public void deleteGraph() throws TransformedGraphException {
		synchronized (_inputGraph) {
			try {
				_inputGraph.workingInputGraphStatus.deleteGraph(this);
				_inputGraph.isDeleted = true;
			} catch (WorkingInputGraphStatus.NotWorkingTransformerException e) {
				throw new TransformedGraphException(NOT_WORKING_TRANSFORMER);
			} catch (Exception e) {
				throw new TransformedGraphException(e);
			}
		}
	}

	@Override
	public boolean isDeleted() {
		synchronized (_inputGraph) {
			return _inputGraph.isDeleted;
		}
	}
}
