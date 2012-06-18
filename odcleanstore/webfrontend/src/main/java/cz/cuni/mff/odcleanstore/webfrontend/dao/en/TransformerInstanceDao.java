package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.io.Serializable;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class TransformerInstanceDao implements Serializable
{
	public static final String TABLE_NAME = Dao.TABLE_NAME_PREFIX + "TRANSFORMERS_TO_PIPELINES_ASSIGNMENT";

	private static final long serialVersionUID = 1L;
	
	private DaoLookupFactory lookupFactory;
	private transient JdbcTemplate jdbcTemplate;
	
	/**
	 * 
	 * @param lookupFactory
	 */
	public void setDaoLookupFactory(DaoLookupFactory lookupFactory)
	{
		this.lookupFactory = lookupFactory;
	}
		
	private JdbcTemplate getJdbcTemplate()
	{
		if (jdbcTemplate == null)
		{
			DataSource dataSource = lookupFactory.getDataSource();
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		
		return jdbcTemplate;
	}
	
	public List<TransformerInstance> loadBy(String columnName, Object value)
	{
		String query =
			"SELECT T.label, TA.* " +
			"FROM " + TransformerDao.TABLE_NAME + " AS T " +
			"JOIN " + TransformerInstanceDao.TABLE_NAME + " AS TA " +
			"ON (T.id = TA.transformerId) " +
			"WHERE TA." + columnName + " = ?";
		
		Object[] params = { value };
		
		return getJdbcTemplate().query(query, params, new TransformerInstanceRowMapper());
	}
	
	public TransformerInstance load(Long pipelineId, Long transformerId)
	{
		String query =
			"SELECT T.label, TA.* " +
			"FROM " + TransformerDao.TABLE_NAME + " AS T " +
			"JOIN " + TransformerInstanceDao.TABLE_NAME + " AS TA " +
			"ON (T.id = TA.transformerId) " +
			"WHERE T.pipelineId = ? AND T.transformerId = ?";
		
		Object[] params = { pipelineId, transformerId };
		
		return getJdbcTemplate().queryForObject(query, params, new TransformerInstanceRowMapper());
	}
	
	public void delete(Long pipelineId, Long transformerId)
	{
		String query = "DELETE FROM " + TABLE_NAME + " WHERE pipelineId = ? AND transformerId = ?";
		Object[] params = { pipelineId, transformerId };
		
		getJdbcTemplate().update(query, params);
	}
	
	public void save(TransformerInstance item)
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (pipelineId, transformerId, workDirPath, configuration, priority) " +
			"VALUES (?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getPipelineId(),
			item.getTransformerId(),
			item.getWorkDirPath(),
			item.getConfiguration(),
			item.getPriority()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
