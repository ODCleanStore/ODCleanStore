package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.GraphInErrorCount;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoSortableDataProvidable;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class GraphInErrorCountDao extends Dao implements DaoSortableDataProvidable<GraphInErrorCount> {

	private static final long serialVersionUID = 1L;
	public static final String INPUT_GRAPHS_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS";
	public static final String INPUT_GRAPHS_STATES_TABLE_NAME = TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";
	public static final String PIPELINES_TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";
	
	private final GraphInErrorCountRowMapper rowMapper;
	
	public GraphInErrorCountDao() {
		rowMapper = new GraphInErrorCountRowMapper();
	}

	public String getTableName() {
		return INPUT_GRAPHS_TABLE_NAME;
	}

	protected GraphInErrorCountRowMapper getRowMapper() {
		return rowMapper;
	}
	
	public List<GraphInErrorCount> loadAllBy (QueryCriteria criteria) {
		String query =
			"SELECT " +
			"iGraph.pipelineId AS pipelineId, " +
			"pipeline.label AS pipelineLabel, " +
			"COUNT(DISTINCT(iGraph.id)) AS graphCount " +
			"FROM " +
			INPUT_GRAPHS_TABLE_NAME + " AS iGraph JOIN " +
			INPUT_GRAPHS_STATES_TABLE_NAME + " AS iState ON iGraph.stateId = iState.id JOIN " +
			PIPELINES_TABLE_NAME + " AS pipeline ON pipeline.id = iGraph.pipelineId " +
			criteria.buildWhereClause() +
			" GROUP BY pipeline.id HAVING graphCount > 0 " +
			criteria.buildOrderByClause();

		Object[] param = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, param, getRowMapper());
	}

	public GraphInErrorCount load(Integer id)
	{
		throw new UnsupportedOperationException();
	}
}