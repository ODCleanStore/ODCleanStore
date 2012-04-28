/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

/**
 * @author jermanp
 * 
 */
public final class TransformedGraphImpl implements TransformedGraph {

	private WorkingInputGraphStatus _workingInputGraphState;
	private String _uuid;
	private LinkedList<String> _attachedGraphNames;
	private boolean _isDeleted;

	TransformedGraphImpl(WorkingInputGraphStatus workingInputGraphState, String uuid) {
		if (workingInputGraphState == null || uuid == null) {
			throw new IllegalArgumentException();
		}

		_workingInputGraphState = workingInputGraphState;
		_uuid = uuid;
	}

	@Override
	public String getGraphName() {
		return Engine.DATA_PREFIX + _uuid;
	}

	@Override
	public String getGraphId() {
		return _uuid;
	}

	@Override
	public String getMetadataGraphName() {
		return Engine.METADATA_PREFIX + _uuid;
	}

	@Override
	public synchronized Collection<String> getAttachedGraphNames() {
		return new LinkedList<String>(_attachedGraphNames);
	}

	@Override
	public synchronized void addAttachedGraph(String attachedGraphName) throws TransformerException {
		if (_isDeleted != true) {
			_workingInputGraphState.addAttachedGraphName(this, attachedGraphName);
			_attachedGraphNames.add(attachedGraphName);
		}
	}

	@Override
	public synchronized void deleteGraph() throws TransformerException {
		if (_isDeleted != true) {
			_workingInputGraphState.delete(this);
			_attachedGraphNames.clear();
			_isDeleted = false;
		}
	}

	@Override
	public synchronized boolean isDeleted() {
		return _isDeleted;
	}
}
