package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;

public class TransformerInstanceDao extends DaoForAuthorableEntity<TransformerInstance>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "TRANSFORMER_INSTANCES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<TransformerInstance> rowMapper;
	
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
	public void save(TransformerInstance item) throws Exception
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
		
		jdbcUpdate(query, params);
	}
	
	public void update(TransformerInstance item) throws Exception
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
}
