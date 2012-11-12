package cz.cuni.mff.odcleanstore.engine.db.model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.ModelException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.engine.db.DbContext;
import cz.cuni.mff.odcleanstore.model.EnumGraphState;
import cz.cuni.mff.odcleanstore.model.EnumPipelineErrorType;

/**
 * Class for executing Engine queries to the relational database.
 * @see SQL
 * @author Petr Jerman
 */
public class DbOdcsContext extends DbContext {

    private static final String ERROR_CREATE_ODCS_CONTEXT = "Error during creating DbOdcsContext";

    public DbOdcsContext() throws DbOdcsException {
        try {
            setConnection(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
            execute(SQL.USE_ODCS_SCHEMA);
        } catch (Exception e) {
            throw new DbOdcsException(ERROR_CREATE_ODCS_CONTEXT, e);
        }
    }

    public Graph selectOldestEngineWorkingGraph(String engineUuid) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_WORKING_GRAPH, engineUuid);
            return createDbGraph(resultSet);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_WORKING_GRAPH, e);
        } finally {
            close(resultSet);
        }
    }

    public Graph selectOldestEngineQueuedGraph(String engineUuid) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_QUEUD_GRAPH, engineUuid);
            return createDbGraph(resultSet);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_QUEUD_GRAPH, e);
        } finally {
            close(resultSet);
        }
    }

    private Graph createDbGraph(WrappedResultSet resultSet)
            throws ConnectionException, QueryException, ModelException, SQLException {
        if (resultSet.next()) {
            Graph dbGraph = new Graph();
            dbGraph.pipeline = new Pipeline();
            int column = 1;
            dbGraph.id = resultSet.getInt(column++);
            dbGraph.uuid = resultSet.getString(column++);
            dbGraph.state = EnumGraphState.fromId(resultSet.getInt(column++));
            dbGraph.pipeline.id = resultSet.getInt(column++);
            dbGraph.pipeline.label = resultSet.getString(column++);
            dbGraph.isInCleanDb = resultSet.getInt(column++) != 0;
            dbGraph.engineUuid = resultSet.getString(column++);
            dbGraph.resetPipelineRequest = resultSet.getInt(column++) != 0;
            dbGraph.pipeline.authorId = resultSet.getInt(column++);
            return dbGraph;
        }
        return null;
    }
    
    public boolean selectResetPipelineRequest(int graphId) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_GRAPH_RESETPIPELINEREQUEST,  graphId);
            resultSet.next();
            Integer isLocked = resultSet.getInt(2);
            return resultSet.getInt(1) != 0 || (isLocked != 0 && isLocked != null);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_GRAPH_RESETPIPELINEREQUEST, e);
        } finally {
            close(resultSet);
        }
    }
    
    public void clearResetPipelineRequest(int graphId) throws DbOdcsException {
        try {
            execute(SQL.CLEAR_GRAPH_RESETPIPELINEREQUEST, graphId);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_CLEAR_GRAPH_RESETPIPELINEREQUEST, e);
        }
    }

    public boolean updateAttachedEngine(int graphId, String engineUuid) throws DbOdcsException {
        try {
            int updatedRowCount = execute(SQL.UPDATE_ATTACHED_ENGINE, engineUuid, graphId, engineUuid);
            return updatedRowCount > 0;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_UPDATE_ATTACHED_ENGINE, e);
        }
    }

    public boolean updateState(int graphId, EnumGraphState newState) throws DbOdcsException {
        try {
            int updatedRowCount = execute(SQL.UPDATE_GRAPH_STATE, newState.toId(), graphId);
            return updatedRowCount > 0;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_UPDATE_GRAPH_STATE, e);
        }
    }

    public boolean updateStateAndIsInCleanDb(int graphId, EnumGraphState newState, boolean isInCleanDb) throws DbOdcsException {
        try {
            int updatedRowCount = execute(SQL.UPDATE_GRAPH_STATE_AND_ISINCLEANDB, newState.toId(), isInCleanDb ? 1 : 0, graphId);
            return updatedRowCount > 0;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_UPDATE_GRAPH_STATE_AND_ISINCLEANDB, e);
        }
    }

    public HashSet<String> selectAttachedGraphs(int graphId) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            HashSet<String> graphNames = new HashSet<String>();
            resultSet = select(SQL.SELECT_ATTACHED_GRAPHS, graphId);
            while (resultSet.next()) {
                graphNames.add(resultSet.getString(1));
            }
            return graphNames;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_ATTACHED_GRAPHS, e);
        } finally {
            close(resultSet);
        }
    }

    public void insertAttachedGraph(int graphId, String name) throws DbOdcsException {
        try {
            execute(SQL.INSERT_ATTACHED_GRAPH, graphId, name);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_INSERT_ATTACHED_GRAPH, e);
        }
    }

    public void deleteAttachedGraphs(int graphId) throws DbOdcsException {
        try {
            execute(SQL.DELETE_ATTACHED_GRAPHS, graphId);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_DELETE_ATTACHED_GRAPHS, e);
        }
    }

    public PipelineCommand[] selectPipelineCommands(int pipelineId) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            ArrayList<PipelineCommand> dbPipelineCommands = new ArrayList<PipelineCommand>();
            resultSet = select(SQL.SELECT_PIPELINE_COMMANDS, pipelineId);
            while (resultSet.next()) {
                PipelineCommand mbr = new PipelineCommand();
                int column = 1;
                mbr.jarPath = resultSet.getString(column++);
                mbr.fullClassName = resultSet.getString(column++);
                mbr.workDirPath = resultSet.getString(column++);
                String configuration = resultSet.getNString(column++); // configuration should not be null
                mbr.configuration = configuration != null ? configuration : "";
                mbr.runOnCleanDB = resultSet.getInt(column++) != 0;
                mbr.transformerInstanceID = resultSet.getInt(column++);
                mbr.transformerLabel = resultSet.getString(column++);
                mbr.transformerID =  resultSet.getInt(column++);
                dbPipelineCommands.add(mbr);
            }
            return dbPipelineCommands.toArray(new PipelineCommand[0]);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_PIPELINE_COMMANDS, e);
        }
    }

    public GroupRule[] selectDnRules(int pipelineId) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            ArrayList<GroupRule> groupRules = new ArrayList<GroupRule>();
            resultSet = select(SQL.SELECT_DN_GROUPS, pipelineId);
            while (resultSet.next()) {
                GroupRule mbr = new GroupRule();
                mbr.transformerInstanceId = resultSet.getInt(1);
                mbr.groupId = resultSet.getInt(2);
                groupRules.add(mbr);
            }
            return groupRules.toArray(new GroupRule[0]);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_DN_GROUPS, e);
        }
    }

    public GroupRule[] selectQaRules(int pipelineId) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            ArrayList<GroupRule> groupRules = new ArrayList<GroupRule>();
            resultSet = select(SQL.SELECT_QA_GROUPS, pipelineId);
            while (resultSet.next()) {
                GroupRule mbr = new GroupRule();
                mbr.transformerInstanceId = resultSet.getInt(1);
                mbr.groupId = resultSet.getInt(2);
                groupRules.add(mbr);
            }
            return groupRules.toArray(new GroupRule[0]);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_QA_GROUPS, e);
        }
    }

    public GroupRule[] selectOiRules(int pipelineId) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            ArrayList<GroupRule> groupRules = new ArrayList<GroupRule>();
            resultSet = select(SQL.SELECT_OI_GROUPS, pipelineId);
            while (resultSet.next()) {
                GroupRule mbr = new GroupRule();
                mbr.transformerInstanceId = resultSet.getInt(1);
                mbr.groupId = resultSet.getInt(2);
                groupRules.add(mbr);
            }
            return groupRules.toArray(new GroupRule[0]);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_OI_GROUPS, e);
        }
    }

    public Pipeline selectDefaultPipeline() throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_DEFAULT_PIPELINE);
            if (resultSet.next()) {
                Pipeline pipeline = new Pipeline();
                pipeline.id = resultSet.getInt(1);
                pipeline.label = resultSet.getString(2);
                return pipeline;
            }
            return null;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_DEFAULT_PIPELINE, e);
        }
    }
    
    public String selectErrorMessageFromGraphInError(int graphId) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_GRAPH_IN_ERROR, graphId);
            if (resultSet.next()) {
                return  resultSet.getString(1);
            }
            return null;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_GRAPH_IN_ERROR, e);
        }
    }

    public void insertGraphInError(int graphId, EnumPipelineErrorType type, String message) throws DbOdcsException {
        try {
            execute(SQL.INSERT_GRAPH_IN_ERROR, graphId, type.toId(), message);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_INSERT_GRAPH_IN_ERROR, e);
        }
    }

    public void deleteGraphInError(int graphId) throws DbOdcsException {
        try {
            execute(SQL.DELETE_GRAPH_IN_ERROR, graphId);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.DELETE_GRAPH_IN_ERROR, e);
        }
    }
    
    public void insertPipelineResult(int graphId, int pipelineId, Integer pipelineAuthorId, boolean isExistingGraph,
            boolean isSuccess, String errorMessage, Date created) throws DbOdcsException {
        try {
            executeNullsAlllowed(SQL.INSERT_EN_PIPELINE_RESULTS, graphId, pipelineId, pipelineAuthorId, isExistingGraph ? 1 : 0,
                    isSuccess ? 1 : 0, errorMessage, created);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_INSERT_EN_PIPELINE_RESULTS, e);
        }
    }
    
    public Credentials selectScraperCredentials(String userName) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_SCRAPER, userName);
            if (resultSet.next()) {
            	Credentials credential = new Credentials();
            	credential.passwordHash = resultSet.getString(1);
            	credential.salt = resultSet.getString(2);
                return credential;
            }
            return null;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_SCRAPER, e);
        }
    }
    
    public int selectEngineId(String uuid) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_ATTACHED_ENGINE_ID, uuid);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_ATTACHED_ENGINE_ID, e);
        }
    }
    
    public int selectPipelineId(String label) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_PIPELINE_ID, label);
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return 0;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_PIPELINE_ID, e);
        }
    }
    
    public String[] selectAllImportingGraphsForEngine(String engineUuid) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
        	ArrayList<String> uuids = new ArrayList<String>();
            resultSet = select(SQL.SELECT_ALL_IMPORTING_GRAPH, engineUuid);
            while(resultSet.next()) {
            	uuids.add(resultSet.getString(1));
            }
            return uuids.toArray(new String[0]);
 
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_ALL_IMPORTING_GRAPH, e);
        }
    }
    
    public boolean isGraphUuidInSystem(String uuid) throws DbOdcsException {
        WrappedResultSet resultSet = null;
        try {
            resultSet = select(SQL.SELECT_GRAPH_ID, uuid);
            if (resultSet.next()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_SELECT_GRAPH_ID, e);
        }
    }

    public void insertImportingGraph(String uuid, int pipelineId, int engineId) throws DbOdcsException {
        try {
            execute(SQL.INSERT_IMPORTING_GRAPH, uuid, pipelineId, engineId);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_INSERT_IMPORTING_GRAPH, e);
        }
    }    

    public void deleteImportingGraph(String uuid) throws DbOdcsException {
        try {
            execute(SQL.DELETE_IMPORTING_GRAPH, uuid);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.DELETE_IMPORTING_GRAPH, e);
        }
    }
    
    public void updateImportingGraphStateToQueued(String uuid) throws DbOdcsException {
        try {
            execute(SQL.UPDATE_IMPORTING_GRAPH_STATE_TO_QUEUED, uuid);
        } catch (Exception e) {
            throw new DbOdcsException(SQL.ERROR_UPDATE_IMPORTING_GRAPH_STATE_TO_QUEUED, e);
        }
    }
}
