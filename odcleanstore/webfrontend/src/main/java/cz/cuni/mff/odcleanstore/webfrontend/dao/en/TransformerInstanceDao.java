package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.QueryCriteria;

public class TransformerInstanceDao extends DaoForEntityWithSurrogateKey<TransformerInstance>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + BACKUP_TABLE_PREFIX + "TRANSFORMER_INSTANCES";

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
	public List<TransformerInstance> loadAllBy(QueryCriteria criteria)
	{
		String query = 
			"SELECT T.label, TI.* FROM " + getTableName() + " AS TI " +
			"JOIN " + TransformerDao.TABLE_NAME + " AS T ON (T.id = TI.transformerId) " +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();
		
		Object[] params = criteria.buildWhereClauseParams();
		
		return getJdbcTemplate().query(query, params, getRowMapper());
	}
	
	@Override
	public TransformerInstance load(Long id)
	{
		String query = 
			"SELECT T.label, TI.* FROM " + getTableName() + " AS TI " +
			"JOIN " + TransformerDao.TABLE_NAME + " AS T ON (T.id = TI.transformerId) " +
			"WHERE TI.id = ?";
		
		Object[] params = { id };
		
		return getJdbcTemplate().queryForObject(query, params, getRowMapper());
	}
	
	public void save(TransformerInstance item)
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " " +
			"(pipelineId, transformerId, workDirPath, configuration, runOnCleanDB, priority) " +
			"VALUES (?, ?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getPipelineId(),
			item.getTransformerId(),
			item.getWorkDirPath(),
			item.getConfiguration(),
			boolToSmallint(item.getRunOnCleanDB()),
			item.getPriority()
		};
		
		getJdbcTemplate().update(query, params);
	}
	
	public void update(TransformerInstance item)
	{
		String query = 
			"UPDATE " + TABLE_NAME + 
			" SET workDirPath = ?, configuration = ?, runOnCleanDB = ?, priority = ? " +
			"WHERE id = ?";
		
		Object[] params =
		{
			item.getWorkDirPath(),
			item.getConfiguration(),
			boolToSmallint(item.getRunOnCleanDB()),
			item.getPriority(),
			item.getId()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
