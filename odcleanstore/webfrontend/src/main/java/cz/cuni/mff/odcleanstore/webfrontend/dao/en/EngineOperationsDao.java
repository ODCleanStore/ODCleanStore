package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.io.Serializable;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class EngineOperationsDao implements Serializable
{
	private static final long serialVersionUID = 1L;

	public enum RULES_GROUPS_TYPE { OI, QA, DN };
	
	private static final String INPUT_GRAPHS_TABLE_NAME = Dao.TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS";
	private static final String INPUT_GRAPHS_STATES_TABLE_NAME = Dao.TABLE_NAME_PREFIX + "EN_INPUT_GRAPHS_STATES";
	
	private static final String QUEUED_STATE_LABEL = "QUEUED";
	private static final String FINISHED_STATE_LABEL = "FINISHED";
	
	private static Logger logger = Logger.getLogger(EngineOperationsDao.class);
	
	protected DaoLookupFactory lookupFactory;
	private transient JdbcTemplate jdbcTemplate;
	
	/**
	 * 
	 * @param lookupFactory
	 */
	public void setDaoLookupFactory(DaoLookupFactory lookupFactory)
	{
		this.lookupFactory = lookupFactory;
	}
	
	/**
	 * 
	 * @return
	 */
	protected JdbcTemplate getJdbcTemplate()
	{
		if (jdbcTemplate == null)
		{
			DataSource dataSource = lookupFactory.getCleanDataSource();
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		
		return jdbcTemplate;
	}
	
	/**
	 * Updates DB contents to signal Engine to rerun all graphs associated with the pipeline given by id.
	 * 
	 * @param pipelineId
	 */
	public void rerunGraphsForPipeline(final Long pipelineId)
	{
		String query =
			"UPDATE " + INPUT_GRAPHS_TABLE_NAME + " " +
			"SET stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?) " +
			"WHERE " +
			"	pipelineId = ? AND " +
			"	stateId = (SELECT id FROM " + INPUT_GRAPHS_STATES_TABLE_NAME + " WHERE label = ?)";
		
		Object[] params = { QUEUED_STATE_LABEL, pipelineId, FINISHED_STATE_LABEL };
		
		logger.debug("queued state label: " + QUEUED_STATE_LABEL);
		logger.debug("pipeline id: " + pipelineId);
		logger.debug("finished state label: " + FINISHED_STATE_LABEL);
		
		getJdbcTemplate().update(query, params);
	}
	
	/**
	 * Updates DB contents to signal Engine to rerun all graphs associated with all pipelines
	 * that have a transformer which is assigned the rule group given by id.
	 * 
	 * @param assignmentDao
	 * @param groupId
	 */
	public void rerunGraphsForRulesGroup(final String assignmentTableName, final Long groupId)
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
		
		Object[] params = { QUEUED_STATE_LABEL, FINISHED_STATE_LABEL, groupId };
		
		logger.debug("queued state label: " + QUEUED_STATE_LABEL);
		logger.debug("finished state label: " + FINISHED_STATE_LABEL);
		logger.debug("group id: " + groupId);
		
		getJdbcTemplate().update(query, params);
	}
}
