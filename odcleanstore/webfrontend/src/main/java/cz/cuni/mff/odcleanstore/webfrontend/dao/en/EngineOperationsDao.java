package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class EngineOperationsDao extends Dao
{
	private static final long serialVersionUID = 1L;

	public enum RULES_GROUPS_TYPE { OI, QA, DN };
	
	/** 
	 * Enum with some graph states (note that not all states are included).
	 */
	private enum EnumGraphState {
		QUEUED, 
		QUEUED_FOR_DELETE,
		FINISHED,
		WRONG
	}
	
	private static final String INPUT_GRAPHS_TABLE_NAME = Dao.TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS";
	private static final String INPUT_GRAPHS_STATES_TABLE_NAME = Dao.TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";
	
	private static Logger logger = Logger.getLogger(EngineOperationsDao.class);
	
	/**
	 * Updates DB contents to signal Engine to rerun all graphs associated with the pipeline given by id.
	 * 
	 * @param pipelineId
	 */
	public void rerunGraphsForPipeline(final Integer pipelineId) throws Exception
	{
		String query =
			"UPDATE " + INPUT_GRAPHS_TABLE_NAME + " " +
			"SET stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?) " +
			"WHERE " +
			"	pipelineId = ? AND " +
			"	stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?)";
		
		Object[] params = { EnumGraphState.QUEUED, pipelineId, EnumGraphState.FINISHED };
		
		logger.debug("queued state label: " + EnumGraphState.QUEUED);
		logger.debug("pipeline id: " + pipelineId);
		logger.debug("finished state label: " + EnumGraphState.FINISHED);
		
		jdbcUpdate(query, params);
	}
	
	/**
	 * Updates DB contents to signal Engine to rerun all graphs associated with all pipelines
	 * that have a transformer which is assigned the rule group given by id.
	 * 
	 * @param assignmentDao
	 * @param groupId
	 */
	public void rerunGraphsForRulesGroup(final String assignmentTableName, final Integer groupId) throws Exception
	{
		String query =
			"UPDATE " + INPUT_GRAPHS_TABLE_NAME + " " +
			"SET stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?) " +
			"WHERE " +
			"	stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?) AND " +
			"	pipelineId IN (" +
			"		SELECT DISTINCT TI.pipelineId " +
			"		FROM " + assignmentTableName + " as RA " +
			"		JOIN " + TransformerInstanceDao.TABLE_NAME + " as TI " +
			"		ON (RA.transformerInstanceId = TI.id) " +
			"		WHERE (RA.groupId = ?) " +
			"	)";
		
		Object[] params = { EnumGraphState.QUEUED, EnumGraphState.FINISHED, groupId };
		
		logger.debug("queued state label: " + EnumGraphState.QUEUED);
		logger.debug("finished state label: " + EnumGraphState.FINISHED);
		logger.debug("group id: " + groupId);
		
		jdbcUpdate(query, params);
	}
	
	/**
	 * Updates DB contents to signal Engine to rerun pipeline on graph with the given id.
	 * 
	 * @param graphId ID of graph to re-run pipeline on
	 */
	public void rerunGraph(final Integer graphId) throws Exception
	{
		String query =
			"UPDATE " + INPUT_GRAPHS_TABLE_NAME + " " +
			"SET stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?) " +
			"WHERE id = ? AND " +
			"	stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?)";
		
		logger.debug("graph id: " + graphId);
		
		jdbcUpdate(query, EnumGraphState.QUEUED, graphId, EnumGraphState.FINISHED);
	}
	
	
	/**
	 * Marks graph for deletion by Engine. Should be called only on graphs in state FINISHED or WRONG.
	 * 
	 * @param graphId ID of graph to be deleted
	 */
	public void queueGraphForDeletion(final Integer graphId) throws Exception
	{
		String query =
			"UPDATE " + INPUT_GRAPHS_TABLE_NAME + " " +
			"SET stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?) " +
			"WHERE id = ? AND " +
			"	stateId IN (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ? OR label = ?)";
		
		logger.debug("graph id: " + graphId);
		
		jdbcUpdate(query, EnumGraphState.QUEUED_FOR_DELETE, graphId, EnumGraphState.FINISHED, EnumGraphState.WRONG);
	}
}
