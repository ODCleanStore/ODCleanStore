package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Date;
import java.util.HashSet;

import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.DbTransactionException;
import cz.cuni.mff.odcleanstore.engine.db.model.DbOdcsContext;
import cz.cuni.mff.odcleanstore.engine.db.model.Graph;
import cz.cuni.mff.odcleanstore.engine.db.model.GraphStates;
import cz.cuni.mff.odcleanstore.engine.db.model.GroupRule;
import cz.cuni.mff.odcleanstore.engine.db.model.Pipeline;
import cz.cuni.mff.odcleanstore.engine.db.model.PipelineCommand;
import cz.cuni.mff.odcleanstore.engine.db.model.PipelineErrorTypes;

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

    private static final Object lockForGetNextGraphForPipeline = new Object();

    static PipelineGraphStatus getNextGraphForPipeline(String engineUuid) throws PipelineGraphStatusException {
        synchronized (lockForGetNextGraphForPipeline) {
            DbOdcsContext context = null;
            try {
                context = new DbOdcsContext();
                while (true) {
                    Graph graph = context.selectOldestEngineWorkingGraph(engineUuid);
                    if (graph == null) {
                        graph = context.selectOldestEngineQueuedGraph(engineUuid);
                        if (graph == null) {
                            return null;
                        }
                        switch (graph.state) {
                        case QUEUED_FOR_DELETE:
                            context.updateState(graph.id, GraphStates.DELETING);
                            graph.state = GraphStates.DELETING;
                            break;
                        case QUEUED_URGENT:
                            context.updateState(graph.id, GraphStates.PROCESSING);
                            context.clearResetPipelineRequest(graph.id);
                            graph.resetPipelineRequest = false;
                            graph.state = GraphStates.PROCESSING;
                            break;
                        case QUEUED:
                            context.updateState(graph.id, GraphStates.PROCESSING);
                            context.clearResetPipelineRequest(graph.id);
                            graph.resetPipelineRequest = false;
                            graph.state = GraphStates.PROCESSING;
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

    private PipelineGraphStatus(DbOdcsContext context, Graph dbGraph) throws Exception {
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

    String getUuid() {
        return graph.uuid;
    }

    GraphStates getState() {
        return graph.state;
    }

    boolean isInCleanDb() {
        return graph.isInCleanDb;
    }
    
    boolean isInCleanDbBeforeProcessing() {
        return isInCleanDbBeforeProcessing;
    }
    
    boolean isResetPipelineRequest() {
        return graph.resetPipelineRequest;
    }

    Integer getPipelineId() {
        return new Integer(pipeline.id);
    }

    String getPipelineLabel() {
        return pipeline.label;
    }

    @SuppressWarnings("unchecked")
    HashSet<String> getAttachedGraphs() {
        return (HashSet<String>) attachedGraphs.clone();
    }

    PipelineCommand[] getPipelineCommands() {
        return PipelineCommand.deepClone(pipelineCommands);
    }

    Integer[] getQaGroups(int transformerInstanceId) {
        return GroupRule.selectDeepClone(qaRules, transformerInstanceId);
    }

    Integer[] getDnGroups(int transformerInstanceId) {
        return GroupRule.selectDeepClone(dnRules, transformerInstanceId);
    }

    Integer[] getOiGroups(int transformerInstanceId) {
        return GroupRule.selectDeepClone(oiRules, transformerInstanceId);
    }

    void markForDeleting() {
        markedForDeleting = true;
    }

    boolean isMarkedForDeleting() {
        return markedForDeleting;
    }

    GraphStates setNoDirtyState(GraphStates state) throws PipelineGraphStatusException {
        DbOdcsContext context = null;
        assert state != GraphStates.DIRTY;
        try {
            context = new DbOdcsContext();
            
            boolean isResetPipelineState = context.selectResetPipelineRequest(graph.id);

            if (state == GraphStates.DELETED || (state == GraphStates.WRONG && !graph.isInCleanDb)) {
                context.deleteAttachedGraphs(graph.id);
            }
            
            if (state == GraphStates.NEWGRAPHSPREPARED) {
                context.updateStateAndIsInCleanDb(graph.id, state, true);
            } else if (state == GraphStates.DELETING) {
                context.updateStateAndIsInCleanDb(graph.id, state, false);
            } else if (state == GraphStates.WRONG && isResetPipelineState) {
            	context.deleteGraphInError(graph.id);
            	context.updateState(graph.id, GraphStates.QUEUED);
            } else if (state == GraphStates.FINISHED && isResetPipelineState) {
            	context.updateState(graph.id, GraphStates.QUEUED);
            } else if (state == GraphStates.FINISHED) {
            	context.insertPipelineResult(graph.id, graph.pipeline.id, graph.pipeline.authorId, isInCleanDbBeforeProcessing , true, null, new Date());
            	context.updateState(graph.id, state);
            } else if (state == GraphStates.WRONG) {
            	String errorMessage = context.selectErrorMessageFromGraphInError(graph.id);
            	context.insertPipelineResult(graph.id, graph.pipeline.id, graph.pipeline.authorId, isInCleanDbBeforeProcessing , false, errorMessage, new Date());
            	context.updateState(graph.id, state);
            } else {
                context.updateState(graph.id, state);
            }

            context.commit();

            graph.state = state;

            if (state == GraphStates.DELETED || (state == GraphStates.WRONG && !graph.isInCleanDb)) {
                attachedGraphs.clear();
            }
            
            if (state == GraphStates.NEWGRAPHSPREPARED) {
                graph.isInCleanDb = true;
            } else if (state == GraphStates.DELETING) {
                graph.isInCleanDb = false;
        	} else if (state == GraphStates.WRONG && isResetPipelineState) {
        		state = GraphStates.QUEUED;
        	} else if (state == GraphStates.FINISHED && isResetPipelineState) {
        		state = GraphStates.QUEUED;
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

    void setDirtyState(PipelineErrorTypes pipelineErrorType, String message) throws PipelineGraphStatusException {
        DbOdcsContext context = null;
        try {
            context = new DbOdcsContext();
            context.insertGraphInError(graph.id, pipelineErrorType, message);
            context.updateState(graph.id, GraphStates.DIRTY);
            context.commit();
            graph.state = GraphStates.DIRTY;
        } catch (Exception e) {
            throw new PipelineGraphStatusException(format(ERROR_SET_STATE, GraphStates.DIRTY), e);
        } finally {
            if (context != null) {
                context.closeQuietly();
            }
        }
    }
    
    void checkResetPipelineRequest() throws PipelineGraphStatusException {
    	DbOdcsContext context = null;
        try {
            context = new DbOdcsContext();
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
    
    void addAttachedGraph(String name) throws PipelineGraphStatusException {
        DbOdcsContext context = null;
        try {
            if (attachedGraphs.contains(name)) {
                return;
            }
            context = new DbOdcsContext();
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

    void deleteAttachedGraphs() throws PipelineGraphStatusException {
        DbOdcsContext context = null;
        try {
            context = new DbOdcsContext();
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

    private String format(String message, GraphStates state) {
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

