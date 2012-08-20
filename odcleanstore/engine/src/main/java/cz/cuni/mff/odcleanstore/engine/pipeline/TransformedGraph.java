package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;

/**
 *  @author Petr Jerman
 */
public final class TransformedGraph implements cz.cuni.mff.odcleanstore.transformer.TransformedGraph {

	private static final Logger LOG = Logger.getLogger(TransformedGraph.class);
	
	public static final String ERROR_NOT_ACTIVE_PIPELINE = "Operation is permitted only for transformed graph in active pipeline";
	public static final String ERROR_ATTACH_GRAPH = "Error during adding attached graph";

	private PipelineGraphStatus graphStatus;
	
	TransformedGraph(PipelineGraphStatus graphStatus) {
		assert graphStatus != null;
		this.graphStatus = graphStatus;
	}
	
	void deactivate() {
		this.graphStatus = null;
	}
	
	@Override
	public String getGraphName() {
		PipelineGraphStatus graphStatus = this.graphStatus;
		if(graphStatus == null) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return ConfigLoader.getConfig().getBackendGroup().getDataGraphURIPrefix() + graphStatus.getUuid();
	}

	@Override
	public String getGraphId() {
		PipelineGraphStatus graphStatus = this.graphStatus;
		if(graphStatus == null) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return graphStatus.getUuid();
	}

	@Override
	public String getMetadataGraphName() {
		PipelineGraphStatus graphStatus = this.graphStatus;
		if(graphStatus == null) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return ConfigLoader.getConfig().getBackendGroup().getMetadataGraphURIPrefix() + graphStatus.getUuid();
	}

	@Override
	public Collection<String> getAttachedGraphNames() {
		PipelineGraphStatus graphStatus = this.graphStatus;
		if(graphStatus == null) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return graphStatus.getAttachedGraphs();
	}

	@Override
	public void addAttachedGraph(String attachedGraphName) throws TransformedGraphException {
		PipelineGraphStatus graphStatus = this.graphStatus;
		if (graphStatus == null) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		try {
			graphStatus.addAttachedGraph(attachedGraphName);
		} catch (PipelineGraphStatusException e) {
			LOG.error(ERROR_ATTACH_GRAPH);
			throw new TransformedGraphException(ERROR_ATTACH_GRAPH, e);
		}
	}

	@Override
	public void deleteGraph() throws TransformedGraphException {
		PipelineGraphStatus graphStatus = this.graphStatus;
		if (graphStatus == null) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		graphStatus.markForDeleting();
	}

	@Override
	public boolean isDeleted() {
		PipelineGraphStatus graphStatus = this.graphStatus;
		if(graphStatus == null) {
			LOG.error(ERROR_NOT_ACTIVE_PIPELINE);
			throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_PIPELINE);
		}
		return graphStatus.isMarkedForDeleting();
	}
}
