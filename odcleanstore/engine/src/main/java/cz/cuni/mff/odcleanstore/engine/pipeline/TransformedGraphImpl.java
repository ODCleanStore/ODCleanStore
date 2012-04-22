/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;

/**
 * @author jermanp
 * 
 */
public class TransformedGraphImpl implements TransformedGraph {

	private boolean _isDeleted;

	TransformedGraphImpl() {
	}

	@Override
	public String getGraphName() {
		String uuid = getGraphId();
		return uuid != null ? Engine.DATA_PREFIX + uuid : null;
	}

	@Override
	public String getGraphId() {
		WorkingInputGraphState workingInputGraphState = PipelineService.getWorkingInputGraphState(this);
		return workingInputGraphState != null ? workingInputGraphState.getUuid() : null;
	}

	@Override
	public String getMetadataGraphName() {
		String uuid = getGraphId();
		return uuid != null ? Engine.METADATA_PREFIX + uuid : null;
	}

	@Override
	public Collection<String> getAttachedGraphNames() {
		WorkingInputGraphState workingInputGraphState = PipelineService.getWorkingInputGraphState(this);
		return workingInputGraphState != null ? workingInputGraphState.getAttachedGraphNames() : null;
	}

	@Override
	public void addAttachedGraph(String attachedGraphName) {
		WorkingInputGraphState workingInputGraphState = PipelineService.getWorkingInputGraphState(this);
		if (workingInputGraphState != null) {
			workingInputGraphState.addAttachedGraphName(attachedGraphName);
		}
	}

	@Override
	public void deleteGraph() {
		_isDeleted = true;
		WorkingInputGraphState workingInputGraphState = PipelineService.getWorkingInputGraphState(this);
		if (workingInputGraphState != null) {
			workingInputGraphState.delete();
		}
	}

	@Override
	public boolean isDeleted() {
		return _isDeleted;
	}
}
