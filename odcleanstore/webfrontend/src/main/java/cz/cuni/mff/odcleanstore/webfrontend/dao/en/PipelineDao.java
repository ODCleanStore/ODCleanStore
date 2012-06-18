package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class PipelineDao extends Dao<Pipeline>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";
	
	private ParameterizedRowMapper<Pipeline> rowMapper;
	
	public PipelineDao()
	{
		this.rowMapper = new PipelineRowMapper();
	}
	
	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<Pipeline> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public Pipeline load(Long id)
	{
		Pipeline pipeline = loadRaw(id);
		pipeline.setTransformers(loadTransformers(id));
		return pipeline;
	}
	
	private List<TransformerInstance> loadTransformers(Long pipelineId)
	{
		String query = 
			"SELECT T.label, TA.* " +
			"FROM " + TransformerDao.TABLE_NAME + " AS T " +
			"JOIN " + TransformerInstanceDao.TABLE_NAME + " AS TA " +
			"ON (T.id = TA.transformerId) " +
			"WHERE TA.pipelineId = ? " +
			"ORDER BY TA.priority";
		
		Object[] params = { pipelineId };
		
		return getJdbcTemplate().query(query, params, new TransformerInstanceRowMapper());
	}
	
	@Override
	public void save(Pipeline item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (label, description, runOnCleanDB) " +
			"VALUES (?, ?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			Boolean.FALSE
		};
		
		getJdbcTemplate().update(query, params);
	}
	
	@Override
	public void update(Pipeline item)
	{
		if (!item.getRunOnCleanDB())
			return;
		
		dropRunOnCleanDBForAllRows();
		setRunOnCleanDB(item.getId());
	}
	
	private void dropRunOnCleanDBForAllRows()
	{
		String query = "UPDATE " + TABLE_NAME + " SET runOnCleanDB = 0";
		getJdbcTemplate().update(query);
	}
	
	private void setRunOnCleanDB(Long pipelineId)
	{
		String query = "UPDATE " + TABLE_NAME + " SET runOnCleanDB = 1 WHERE id = ?";
		Object[] params = { pipelineId };
		
		getJdbcTemplate().update(query, params);
	}
	
	@Override
	public void delete(Pipeline item)
	{
		deleteRelatedPipelinesMapping(item.getId());
		deleteRaw(item.getId());
	}
	
	private void deleteRelatedPipelinesMapping(Long pipelineId)
	{
		String query = "DELETE FROM " + TransformerInstanceDao.TABLE_NAME + " WHERE pipelineId = ?";
		Object[] params = { pipelineId };

		getJdbcTemplate().update(query, params);
	}
}
