package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class TransformerInstanceDao 
{
	public static final String TABLE_NAME = Dao.TABLE_NAME_PREFIX + "TRANSFORMERS_TO_PIPELINES_ASSIGNMENT";
	
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource)
	{
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void delete(Long pipelineId, Long transformerId)
	{
		String query = "DELETE FROM " + TABLE_NAME + " WHERE pipelineId = ? AND transformerId = ?";
		Object[] params = { pipelineId, transformerId };
		
		jdbcTemplate.update(query, params);
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
		
		jdbcTemplate.update(query, params);
	}
}
