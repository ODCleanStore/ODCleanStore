package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInErrorCount;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class GraphInErrorCountDao extends DaoForEntityWithSurrogateKey<GraphInErrorCount> {

	private static final long serialVersionUID = 1L;
	public static final String GRAPHS_IN_ERROR_TABLE_NAME = TABLE_NAME_PREFIX + "EN_GRAPHS_IN_ERROR";
	public static final String INPUT_GRAPHS_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS";
	public static final String INPUT_GRAPHS_STATES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";
	public static final String PIPELINES_TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";

	@Override
	public String getTableName() {
		return GRAPHS_IN_ERROR_TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<GraphInErrorCount> getRowMapper() {
		return new GraphInErrorCountRowMapper();
	}

	@Override
	public List<GraphInErrorCount> loadAllBy (QueryCriteria criteria) {
		String query =
			"SELECT " +
			"iGraph.pipelineId AS pipelineId, " +
			"COUNT(eGraph.id) AS graphCount, " +
			"pipeline.label AS pipelineLabel " +
			"FROM " + GRAPHS_IN_ERROR_TABLE_NAME + " AS eGraph JOIN " +
			INPUT_GRAPHS_TABLE_NAME + " AS iGraph ON eGraph.graphId = iGraph.id JOIN " +
			INPUT_GRAPHS_STATES_TABLE_NAME + " AS iState ON iGraph.stateId = iState.id JOIN " +
			PIPELINES_TABLE_NAME + " AS pipeline ON pipeline.id = iGraph.pipelineId " +
			criteria.buildWhereClause() +
			" GROUP BY pipeline.id HAVING graphCount > 0 " +
			criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, param, getRowMapper());
	}
}