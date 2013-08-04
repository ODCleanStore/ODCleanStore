package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

/**
 * Container for all necessary information about a transformed graph for a custom transformer.
 * 
 *  @author Petr Jerman
 */
public final class TransformedGraph implements cz.cuni.mff.odcleanstore.transformer.TransformedGraph {

    private static final Logger LOG = LoggerFactory.getLogger(TransformedGraph.class);
    
    public static final String ERROR_NOT_ACTIVE_TRANSFORMER =
            "Operation is permitted only for active transformation context in active pipeline";
    public static final String ERROR_ATTACH_GRAPH = "Error during adding attached graph";

    private PipelineGraphStatus graphStatus;
    
    TransformedGraph(PipelineGraphStatus graphStatus) {
        assert graphStatus != null;
        this.graphStatus = graphStatus;
    }
    
    /**
* 	Deactivate object, calling deactivated object method caused exception. 
     */
    void deactivate() {
        this.graphStatus = null;
    }
    
    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#getGraphName()
     */
    @Override
    public String getGraphName() {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return graphStatus.getNamedGraphsPrefix() + ODCSInternal.DATA_GRAPH_URI_INFIX + graphStatus.getUuid();
    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#getGraphId()
     */
    @Override
    public String getGraphId() {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return graphStatus.getUuid();
    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#getMetadataGraphName()
     */
    @Override
    public String getMetadataGraphName() {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return graphStatus.getNamedGraphsPrefix() + ODCSInternal.METADATA_GRAPH_URI_INFIX + graphStatus.getUuid();
    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#getProvenanceMetadataGraphName()
     */
    @Override
    public String getProvenanceMetadataGraphName() {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return graphStatus.getNamedGraphsPrefix() + ODCSInternal.PROVENANCE_METADATA_GRAPH_URI_INFIX + graphStatus.getUuid();
    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#getAttachedGraphNames()
     */
    @Override
    public Collection<String> getAttachedGraphNames() {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return graphStatus.getAttachedGraphs();
    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#addAttachedGraph(java.lang.String)
     */
    @Override
    public void addAttachedGraph(String attachedGraphName) throws TransformedGraphException {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        try {
            graphStatus.addAttachedGraph(attachedGraphName);
        } catch (PipelineGraphStatusException e) {
            LOG.error(ERROR_ATTACH_GRAPH);
            throw new TransformedGraphException(ERROR_ATTACH_GRAPH, e);
        }
    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#deleteGraph()
     */
    @Override
    public void deleteGraph() throws TransformedGraphException {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        graphStatus.markForDeleting();
    }

    /**
     * @see cz.cuni.mff.odcleanstore.transformer.TransformedGraph#isDeleted()
     */
    @Override
    public boolean isDeleted() {
        PipelineGraphStatus graphStatus = this.graphStatus;
        if (graphStatus == null) {
            LOG.error(ERROR_NOT_ACTIVE_TRANSFORMER);
            throw new TransformedGraphRuntimeException(ERROR_NOT_ACTIVE_TRANSFORMER);
        }
        return graphStatus.isMarkedForDeleting();
    }
}
