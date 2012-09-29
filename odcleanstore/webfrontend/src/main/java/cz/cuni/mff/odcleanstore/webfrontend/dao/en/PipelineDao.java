package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Pipeline;
import cz.cuni.mff.odcleanstore.webfrontend.bo.en.TransformerInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class PipelineDao extends DaoForEntityWithSurrogateKey<Pipeline>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "PIPELINES";
	
	private static final long serialVersionUID = 1L;
	
	private ParameterizedRowMapper<Pipeline> rowMapper;
	
	public PipelineDao()
	{
		this.rowMapper = new PipelineRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<Pipeline> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public Pipeline load(Integer id)
	{
		// TODO: lazy loading?
		Pipeline pipeline = super.load(id);
		pipeline.setTransformers(loadTransformers(id));
		return pipeline;
	}
	
	private List<TransformerInstance> loadTransformers(Integer pipelineId)
	{
		String query = 
			"SELECT T.label, TA.* " +
			"FROM " + TransformerDao.TABLE_NAME + " AS T " +
			"JOIN " + TransformerInstanceDao.TABLE_NAME + " AS TA " +
			"ON (T.id = TA.transformerId) " +
			"WHERE TA.pipelineId = ? " +
			"ORDER BY TA.priority";
		
		Object[] params = { pipelineId };
		
		return jdbcQuery(query, params, new TransformerInstanceRowMapper());
	}
	
	@Override
	public void save(Pipeline item)  throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (label, description, isDefault, authorId) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			Boolean.FALSE,
			item.getAuthorId()
		};
		
		jdbcUpdate(query, params);
	}
	
	public void update(final Pipeline item) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				updateRaw(item);
				if (item.isDefault())
				{
					dropRunOnCleanDBForAllRows();
					setRunOnCleanDB(item.getId());
				}
			}
		});
		
	}
	
	private void dropRunOnCleanDBForAllRows()  throws Exception
	{
		String query = "UPDATE " + TABLE_NAME + " SET isDefault = 0";
		jdbcUpdate(query);
	}
	
	private void setRunOnCleanDB(Integer pipelineId) throws Exception
	{
		String query = "UPDATE " + TABLE_NAME + " SET isDefault = 1 WHERE id = ?";
		Object[] params = { pipelineId };
		
		jdbcUpdate(query, params);
	}
	
	private void updateRaw(Pipeline item) throws Exception
	{
		// TODO: method setLocked() would be nicer
		String query = "UPDATE " + TABLE_NAME + " SET label = ?, description = ?, isLocked = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.isLocked(),
			item.getId()
		};
		
		jdbcUpdate(query, params);
	}
}
