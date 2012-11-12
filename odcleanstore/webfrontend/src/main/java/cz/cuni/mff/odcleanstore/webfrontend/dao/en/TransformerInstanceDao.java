package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;
import cz.cuni.mff.odcleanstore.webfrontend.util.CodeSnippet;

/**
 * The Transformer instance Row Mapper.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class TransformerInstanceDao extends DaoForAuthorableEntity<TransformerInstance>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "TRANSFORMER_INSTANCES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<TransformerInstance> rowMapper;
	
	/**
	 * 
	 */
	public TransformerInstanceDao()
	{
		this.rowMapper = new TransformerInstanceRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<TransformerInstance> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	protected String getSelectAndFromClause()
	{
		return "SELECT T.label, TI.* FROM " + getTableName() + " AS TI " +
			"JOIN " + TransformerDao.TABLE_NAME + " AS T ON (T.id = TI.transformerId) ";
	}
	
	@Override
	public TransformerInstance load(Integer id)
	{
		return loadBy("TI.id", id);
	}

	@Override
	protected void deleteRaw(final Integer id) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				TransformerInstance item = load(id);
				shiftPrioritiesDownFrom(item.getPipelineId(), item.getPriority());
				TransformerInstanceDao.super.deleteRaw(id);
			}
		});
	}
	
	@Override
	public void save(final TransformerInstance item) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				shiftPrioritiesUpFrom(item.getPipelineId(), item.getPriority());
				saveRaw(item);
			}
		});
	}
	
	/**
	 * 
	 * @param pipelineId
	 * @param priority
	 * @throws Exception
	 */
	private void shiftPrioritiesUpFrom(Integer pipelineId, Integer priority) throws Exception
	{
		String query = 
			"UPDATE " + TABLE_NAME + 
			" SET priority = priority + 1 " +
			" WHERE pipelineId = ? AND priority >= ?";
		
		jdbcUpdate(query, pipelineId, priority);
	}
	
	/**
	 * 
	 * @param pipelineId
	 * @param priority
	 * @throws Exception
	 */
	private void shiftPrioritiesDownFrom(Integer pipelineId, Integer priority) throws Exception
	{
		String query = 
			"UPDATE " + TABLE_NAME + 
			" SET priority = priority - 1 " +
			" WHERE pipelineId = ? AND priority > ?";
		
		jdbcUpdate(query, pipelineId, priority);
	}
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	private void saveRaw(TransformerInstance item) throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " " +
			"(pipelineId, transformerId, configuration, runOnCleanDB, priority) " +
			"VALUES (?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getPipelineId(),
			item.getTransformerId(),
			item.getConfiguration(),
			boolToSmallint(item.getRunOnCleanDB()),
			item.getPriority()
		};
		
		logger.debug("priority:" + item.getPriority());
		
		jdbcUpdate(query, params);
	}
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	public void update(final TransformerInstance item) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				Integer oldPriority = load(item.getId()).getPriority();
				shiftPrioritiesDownFrom(item.getPipelineId(), oldPriority);
				shiftPrioritiesUpFrom(item.getPipelineId(), item.getPriority());
				updateRaw(item);
			}
		});
	}
	
	/**
	 * 
	 * @param item
	 * @throws Exception
	 */
	private void updateRaw(TransformerInstance item) throws Exception
	{
		String query = 
			"UPDATE " + TABLE_NAME + 
			" SET configuration = ?, runOnCleanDB = ?, priority = ? " +
			"WHERE id = ?";
		
		Object[] params =
		{
			item.getConfiguration(),
			boolToSmallint(item.getRunOnCleanDB()),
			item.getPriority(),
			item.getId()
		};
		
		jdbcUpdate(query, params);
	}

	@Override
	public int getAuthorId(Integer entityId)
	{
		String query = 
			"SELECT P.authorId FROM " + getTableName() + " AS TI " +
			"JOIN " + PipelineDao.TABLE_NAME + " AS P ON (P.id = TI.pipelineId) " +
			"WHERE TI.id = ?";
		
		return jdbcQueryForInt(query, entityId);
	}
	
	/**
	 * 
	 * @param pipelineId
	 * @return
	 */
	public int getInstancesCount(Integer pipelineId)
	{
		String query = "SELECT count(*) FROM " + getTableName() + " WHERE pipelineId = ?";
		return jdbcQueryForInt(query, pipelineId);
	}
}
