package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.DbTransactionException;
import cz.cuni.mff.odcleanstore.engine.db.model.DbOdcsContextSparql;
import cz.cuni.mff.odcleanstore.engine.db.model.DbOdcsContextTransactional;
import cz.cuni.mff.odcleanstore.engine.db.model.Graph;
import cz.cuni.mff.odcleanstore.engine.db.model.GroupRule;
import cz.cuni.mff.odcleanstore.engine.db.model.Pipeline;
import cz.cuni.mff.odcleanstore.engine.db.model.PipelineCommand;
import cz.cuni.mff.odcleanstore.model.EnumGraphState;
import cz.cuni.mff.odcleanstore.model.EnumPipelineErrorType;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

import java.util.Date;
import java.util.HashSet;

/**
 * Class for obtaining, representing and manipulating a graph to be processed.
 * @author Petr Jerman
 */
/**
 * Status of current pipeline graph.
 *
 * @author Petr Jerman
 *
 */
public final class PipelineGraphStatus {

    private static final String ERROR_NEXT_GRAPH_FOR_PIPELINE = "Error during getting next graph for pipeline";
    private static final String ERROR_SELECT_QUEUED_GRAPH = "Error during selecting queued graph";
    private static final String ERROR_SELECT_DEFAULT_PIPELINE = "Error during selecting default pipeline";
    private static final String ERROR_ATTACH_ENGINE = "Error during attaching engine to graph for pipeline";
    private static final String ERROR_ADD_ATTACHED_GRAPH = "Error during attaching graph to pipeline";
    private static final String ERROR_DELETE_ATTACHED_GRAPH = "Error during deleting attached graph";
    private static final String ERROR_SET_STATE = "Error during updating graph state";
    private static final String ERROR_SELECT_RESETPIPELINESTATE = "Error during selecting graph reset pipeline state flag";
    private static final String ERROR_RESETPIPELINESTATE_DETECTED = "Reset pipeline state detected";

    private Graph graph = null;
    private HashSet<String> attachedGraphs = null;
    private Pipeline pipeline = null;
    private PipelineCommand[] pipelineCommands = null;
    private GroupRule[] qaRules = null;
    private GroupRule[] dnRules = null;
    private GroupRule[] oiRules = null;
    private boolean markedForDeleting = false;
    private boolean isInCleanDbBeforeProcessing = false;

    // CHECKSTYLE:OFF
    private static final Object lockForGetNextGraphForPipeline = new Object();
    // CHECKSTYLE:ON

    /**
     * Gets next graph for pipeline execution.
     * 
     * @param engineUuid pipeline engine owner
     * @return graph status object
     * @throws PipelineGraphStatusException
     */
    static PipelineGraphStatus getNextGraphForPipeline(String engineUuid) throws PipelineGraphStatusException {
        synchronized (lockForGetNextGraphForPipeline) {
            DbOdcsContextTransactional context = null;
            try {
                context = new DbOdcsContextTransactional();
                while (true) {
                    Graph graph = context.selectOldestEngineWorkingGraph(engineUuid);
                    if (graph == null) {
                        graph = context.selectOldestEngineQueuedGraph(engineUuid);
                        if (graph == null) {
                            return null;
                        }
                        switch (graph.state) {
                        case QUEUED_FOR_DELETE:
                            context.updateState(graph.id, EnumGraphState.DELETING);
                            graph.state = EnumGraphState.DELETING;
                            break;
                        case QUEUED_URGENT:
                            context.updateState(graph.id, EnumGraphState.PROCESSING);
                            context.clearResetPipelineRequest(graph.id);
                            graph.resetPipelineRequest = false;
                            graph.state = EnumGraphState.PROCESSING;
                            break;
                        case QUEUED:
                            context.updateState(graph.id, EnumGraphState.PROCESSING);
                            context.clearResetPipelineRequest(graph.id);
                            graph.resetPipelineRequest = false;
                            graph.state = EnumGraphState.PROCESSING;
                            break;
                        default:
                            throw new PipelineGraphStatusException(ERROR_SELECT_QUEUED_GRAPH);
                        }
                    }
                    if (graph.engineUuid == null) {
                        if (!context.updateAttachedEngine(graph.id, engineUuid)) {
                            throw new PipelineGraphStatusException(ERROR_ATTACH_ENGINE);
                        }
                    }
                    graph.engineUuid = engineUuid;

                    PipelineGraphStatus pipelineGraphStatus = new PipelineGraphStatus(context, graph);
                    try {
                        context.commit();
                    } catch (DbTransactionException e) {
                        context.rollback();
                        continue;
                    }
                    return pipelineGraphStatus;
                }
            } catch (Exception e) {
                throw new PipelineGraphStatusException(ERROR_NEXT_GRAPH_FOR_PIPELINE, e);
            } finally {
                if (context != null) {
                    context.closeQuietly();
                }
            }
        }
    }

    
    /**
     * Create PipelineGraphStatus instance.
     *  
     * @param context rel. datat context object
     * @param dbGraph graph representation object
     * @throws Exception
     */
    private PipelineGraphStatus(DbOdcsContextTransactional context, Graph dbGraph) throws Exception {
        this.graph = dbGraph;
        this.isInCleanDbBeforeProcessing = dbGraph.isInCleanDb;
        this.attachedGraphs = context.selectAttachedGraphs(dbGraph.id);
        pipeline = graph.pipeline != null ? graph.pipeline : context.selectDefaultPipeline();
        if (pipeline == null) {
            throw new PipelineGraphStatusException(ERROR_SELECT_DEFAULT_PIPELINE);
        }
        pipelineCommands = context.selectPipelineCommands(pipeline.id);
        qaRules = context.selectQaRules(pipeline.id);
        dnRules = context.selectDnRules(pipeline.id);
        oiRules = context.selectOiRules(pipeline.id);
    }

    /**
     * @return get graph uuid
     */
    String getUuid() {
        return graph.uuid;
    }
    
    /**
     * @return get named graphs prefix
     */
    String getNamedGraphsPrefix() {
        return graph.namedGraphsPrefix;
    }

    /**
     * @return get current graph state
     */
    EnumGraphState getState() {
        return graph.state;
    }

    /**
     * @return indicate if graph is currently existing (not new - incoming)
     */
    boolean isInCleanDb() {
        return graph.isInCleanDb;
    }
    
    /**
     * @return indicate if graph was existing (not new - incoming) before processing
     */
    boolean isInCleanDbBeforeProcessing() {
        return isInCleanDbBeforeProcessing;
    }
    
    /**
     * Indicate if reset pipeline request is present
     * 
     * @return
     */
    boolean isResetPipelineRequest() {
        return graph.resetPipelineRequest;
    }

    /**
     * @return get pipeline id
     */
    Integer getPipelineId() {
        return new Integer(pipeline.id);
    }

    /**
     * @return get pipeline label
     */
    String getPipelineLabel() {
        return pipeline.label;
    }

    /**
     * @return get all attached graphs for graph
     */
    @SuppressWarnings("unchecked")
    HashSet<String> getAttachedGraphs() {
        return (HashSet<String>) attachedGraphs.clone();
    }

    /**
     * @return get all pipeline command for graph
     */
    PipelineCommand[] getPipelineCommands() {
        return PipelineCommand.deepClone(pipelineCommands);
    }

    /**
     * Get qa groups for transformer instance.
     * 
     * @param transformerInstanceId id of transformer instance
     * @return array of qa groups
     */
    Integer[] getQaGroups(int transformerInstanceId) {
        return GroupRule.selectDeepClone(qaRules, transformerInstanceId);
    }

    /**
     * Get data normalization groups for transformer instance.
     * 
     * @param transformerInstanceId id of transformer instance
     * @return array of dn groups
     */    Integer[] getDnGroups(int transformerInstanceId) {
        return GroupRule.selectDeepClone(dnRules, transformerInstanceId);
    }

    /**
     * Get object identification groups for transformer instance.
     * 
     * @param transformerInstanceId id of transformer instance
     * @return array of oi groups
     */
    Integer[] getOiGroups(int transformerInstanceId) {
        return GroupRule.selectDeepClone(oiRules, transformerInstanceId);
    }

    /**
     *  mark for deleting
     */
    void markForDeleting() {
        markedForDeleting = true;
    }

    /**
     * @return graph is marked for deleting from transformer flag
     */
    boolean isMarkedForDeleting() {
        return markedForDeleting;
    }

    /**
     * Request for graph state other than dirty. 
     * 
     * @param state requested graph state
     * @return result graph state
     * @throws PipelineGraphStatusException
     */
    EnumGraphState setNoDirtyState(EnumGraphState state) throws PipelineGraphStatusException {
        DbOdcsContextTransactional context = null;
        assert state != EnumGraphState.DIRTY;
        try {
            context = new DbOdcsContextTransactional();
            
            boolean isResetPipelineState = context.selectResetPipelineRequest(graph.id);

            if (state == EnumGraphState.DELETED || (state == EnumGraphState.WRONG && !graph.isInCleanDb)) {
                context.deleteAttachedGraphs(graph.id);
            }
            
            if (state == EnumGraphState.NEWGRAPHSPREPARED) {
                context.updateStateAndIsInCleanDb(graph.id, state, true);
            } else if (state == EnumGraphState.DELETING) {
                context.updateStateAndIsInCleanDb(graph.id, state, false);
            } else if (state == EnumGraphState.WRONG && isResetPipelineState) {
                context.deleteGraphInError(graph.id);
                context.updateState(graph.id, EnumGraphState.QUEUED);
            } else if (state == EnumGraphState.FINISHED && isResetPipelineState) {
                context.updateState(graph.id, EnumGraphState.QUEUED);
            } else if (state == EnumGraphState.FINISHED) {
                context.insertPipelineResult(graph.id, graph.pipeline.id, graph.pipeline.authorId, isInCleanDbBeforeProcessing,
                        true, null, new Date());
                context.updateState(graph.id, state);
            } else if (state == EnumGraphState.WRONG) {
                String errorMessage = context.selectErrorMessageFromGraphInError(graph.id);
                context.insertPipelineResult(graph.id, graph.pipeline.id, graph.pipeline.authorId, isInCleanDbBeforeProcessing,
                        false, errorMessage, new Date());
                context.updateState(graph.id, state);
            } else {
                context.updateState(graph.id, state);
            }

            context.commit();

            graph.state = state;

            if (state == EnumGraphState.DELETED || (state == EnumGraphState.WRONG && !graph.isInCleanDb)) {
                attachedGraphs.clear();
            }
            
            if (state == EnumGraphState.NEWGRAPHSPREPARED) {
                graph.isInCleanDb = true;
            } else if (state == EnumGraphState.DELETING) {
                graph.isInCleanDb = false;
            } else if (state == EnumGraphState.WRONG && isResetPipelineState) {
                state = EnumGraphState.QUEUED;
            } else if (state == EnumGraphState.FINISHED && isResetPipelineState) {
                state = EnumGraphState.QUEUED;
            }
            return state;
        } catch (Exception e) {
           throw new PipelineGraphStatusException(format(ERROR_SET_STATE, state), e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
    }

    /**
     * Set dirty state of graph in database.
     * 
     * @param pipelineErrorType type of pipeline error.
     * @param message message describing the error 
     * @throws PipelineGraphStatusException
     */
    void setDirtyState(EnumPipelineErrorType pipelineErrorType, String message) throws PipelineGraphStatusException {
        DbOdcsContextTransactional context = null;
        try {
            context = new DbOdcsContextTransactional();
            context.insertGraphInError(graph.id, pipelineErrorType, message);
            context.updateState(graph.id, EnumGraphState.DIRTY);
            context.commit();
            graph.state = EnumGraphState.DIRTY;
        } catch (Exception e) {
            throw new PipelineGraphStatusException(format(ERROR_SET_STATE, EnumGraphState.DIRTY), e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
    }

    /**
     * Check if reset pipeline request present in database.
     * 
     * @throws PipelineGraphStatusException
     */
    void checkResetPipelineRequest() throws PipelineGraphStatusException {
        DbOdcsContextTransactional context = null;
        try {
            context = new DbOdcsContextTransactional();
            if (context.selectResetPipelineRequest(graph.id)) {
                graph.resetPipelineRequest = true;
                throw new PipelineGraphStatusException(format(ERROR_RESETPIPELINESTATE_DETECTED));
            }
        } catch (Exception e) {
            throw new PipelineGraphStatusException(format(ERROR_SELECT_RESETPIPELINESTATE), e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
    }
    
    /**
     * Add information of attached graphs to executed graph to database.
     * @param name
     * @throws PipelineGraphStatusException
     */
    void addAttachedGraph(String name) throws PipelineGraphStatusException {
        DbOdcsContextTransactional context = null;
        try {
            if (attachedGraphs.contains(name)) {
                return;
            }
            context = new DbOdcsContextTransactional();
            context.insertAttachedGraph(graph.id, name);
            context.commit();
            attachedGraphs.add(name);
        } catch (Exception e) {
            throw new PipelineGraphStatusException(format(ERROR_ADD_ATTACHED_GRAPH), e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
    }

    /**
     * Delete information of attached graphs to executed graph from database.
     * 
     * @throws PipelineGraphStatusException
     */
    void deleteAttachedGraphs() throws PipelineGraphStatusException {
        DbOdcsContextTransactional context = null;
        try {
            context = new DbOdcsContextTransactional();
            context.deleteAttachedGraphs(graph.id);
            context.commit();
            attachedGraphs.clear();
        } catch (Exception e) {
            throw new PipelineGraphStatusException(format(ERROR_DELETE_ATTACHED_GRAPH), e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
    }

    private String format(String message, EnumGraphState state) {
        try {
            return FormatHelper.formatGraphMessage(message + " " + state.toString(), graph.uuid, isInCleanDbBeforeProcessing);
        } catch (Exception e) {
            return message;
        }
    }

    private String format(String message) {
        try {
            return FormatHelper.formatGraphMessage(message, graph.uuid, isInCleanDbBeforeProcessing);
        } catch (Exception e) {
            return message;
        }
    }
}

