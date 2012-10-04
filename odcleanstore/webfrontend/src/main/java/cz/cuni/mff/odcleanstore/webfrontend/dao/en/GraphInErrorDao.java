package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInError;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class GraphInErrorDao extends DaoForEntityWithSurrogateKey<GraphInError> {
	
	private enum EnumGraphState {
		QUEUED {
			public String toString() {
				return "QUEUED";
			}
		},
		QUEUED_FOR_DELETE {
			public String toString() {
				return "QUEUED_FOR_DELETE";
			}
		},
		FINISHED {
			public String toString() {
				return "FINISHED";
			}
		}
	}

	private static final long serialVersionUID = 1L;
	public static final String GRAPHS_IN_ERROR_TABLE_NAME = TABLE_NAME_PREFIX + "EN_GRAPHS_IN_ERROR";
	public static final String INPUT_GRAPHS_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS";
	public static final String INPUT_GRAPHS_STATES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";
	public static final String PIPELINE_ERROR_TYPES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_PIPELINE_ERROR_TYPES";
	public static final String PIPELINES_TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";
	public static final String ATTACHED_ENGINES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_ATTACHED_ENGINES";

	@Override
	public String getTableName() {
		return GRAPHS_IN_ERROR_TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<GraphInError> getRowMapper() {
		return new GraphInErrorRowMapper();
	}

	@Override
	public List<GraphInError> loadAllBy (QueryCriteria criteria) {

		String query =
			"SELECT " +
			"iGraph.engineId AS engineId, " +
			"iGraph.pipelineId AS pipelineId, " +
			"iGraph.uuid AS uuid, " +
			"iGraph.stateId AS stateId, " +
			"iGraph.isInCleanDB AS isInCleanDB, " +
			"eGraph.errorTypeId AS errorTypeId, " +
			"eGraph.errorMessage AS errorMessage," +
			"engine.uuid AS engineUuid, " +
			"pipeline.label AS pipelineLabel, " +
			"iState.label AS stateLabel," +
			"pError.label AS errorTypeLabel " +
			"FROM " +
			INPUT_GRAPHS_TABLE_NAME + " AS iGraph JOIN " +
			INPUT_GRAPHS_STATES_TABLE_NAME + " AS iState ON iGraph.stateId = iState.id JOIN " +
			PIPELINES_TABLE_NAME + " AS pipeline ON pipeline.id = iGraph.pipelineId JOIN " +
			ATTACHED_ENGINES_TABLE_NAME + " AS engine ON engine.id = iGraph.engineId LEFT JOIN " +
			GRAPHS_IN_ERROR_TABLE_NAME + " AS eGraph ON eGraph.graphId = iGraph.id LEFT JOIN " +
			PIPELINE_ERROR_TYPES_TABLE_NAME + " AS pError ON eGraph.errorTypeId = pError.id " +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, param, getRowMapper());
	}
	
	public void markFinished(GraphInError graphInError) throws Exception {
		mark(graphInError, EnumGraphState.FINISHED);
	}
	
	public void markQueued(GraphInError graphInError) throws Exception {
		mark(graphInError, EnumGraphState.QUEUED);
	}
	
	public void markQueuedForDelete(GraphInError graphInError) throws Exception {
		mark(graphInError, EnumGraphState.QUEUED_FOR_DELETE);
	}

	private void mark(GraphInError graphInError, EnumGraphState state) throws Exception {
		QueryCriteria criteria = new QueryCriteria();
		
		criteria.addWhereClause("iGraph.uuid", graphInError.UUID);
		criteria.addWhereClause("state.label", state.toString());

		String query =
				"INSERT REPLACING " + INPUT_GRAPHS_TABLE_NAME + " (id, uuid, stateId, engineId, pipelineId, isInCleanDB) " +
				"SELECT  iGraph.id, iGraph.uuid, state.id, iGraph.engineId, iGraph.pipelineId, iGraph.isInCleanDB " +
				"FROM " +
				INPUT_GRAPHS_TABLE_NAME + " AS iGraph, " +
				INPUT_GRAPHS_STATES_TABLE_NAME + " AS state " +
				criteria.buildWhereClause() +
				criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();

		this.jdbcUpdate(query, param);
	}

	public void markAllQueued(Object[] params) throws Exception {
		markAll(params, EnumGraphState.QUEUED);
	}
	
	public void markAllQueuedForDelete(Object[] params) throws Exception {
		markAll(params, EnumGraphState.QUEUED_FOR_DELETE);
	}
	
	public void markAllFinished(Object[] params) throws Exception {
		markAll(params, EnumGraphState.FINISHED);
	}
	
	private void markAll(Object[] params, EnumGraphState state) throws Exception {
		QueryCriteria criteria = new QueryCriteria();

		for (int i = 0; i < params.length; i += 2) {
			criteria.addWhereClause((String)params[i], params[i + 1]);
		}
		criteria.addWhereClause("iState.label", "WRONG");
		criteria.addWhereClause("state.label", state.toString());

		String query =
				"INSERT REPLACING " + INPUT_GRAPHS_TABLE_NAME + " (id, uuid, stateId, engineId, pipelineId, isInCleanDB) " +
				"SELECT  iGraph.id, iGraph.uuid, state.id, iGraph.engineId, iGraph.pipelineId, iGraph.isInCleanDB " +
				"FROM " +
				INPUT_GRAPHS_TABLE_NAME + " AS iGraph JOIN " +
				INPUT_GRAPHS_STATES_TABLE_NAME + " AS iState ON iGraph.stateId = iState.id, " +
				INPUT_GRAPHS_STATES_TABLE_NAME + " AS state " +
				criteria.buildWhereClause() +
				criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();

		this.jdbcUpdate(query, param);
	}
}
