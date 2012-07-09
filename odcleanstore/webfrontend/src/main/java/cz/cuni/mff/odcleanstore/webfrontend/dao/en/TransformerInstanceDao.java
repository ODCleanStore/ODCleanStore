package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class TransformerInstanceDao extends DaoForEntityWithSurrogateKey<TransformerInstance>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "TRANSFORMER_INSTANCES";

	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<TransformerInstance> rowMapper;
	
	public TransformerInstanceDao()
	{
		this.rowMapper = new TransformerInstanceRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<TransformerInstance> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public List<TransformerInstance> loadAllRawBy(String columnName, Object value)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE " + columnName + " = ? ORDER BY priority";
		Object[] params = { value };
		
		return getJdbcTemplate().query(query, params, getRowMapper());
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
}
