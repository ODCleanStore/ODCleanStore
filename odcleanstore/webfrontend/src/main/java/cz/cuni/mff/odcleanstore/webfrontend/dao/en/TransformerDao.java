package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

public class TransformerDao extends Dao<Transformer> 
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "TRANSFORMERS";

	private static Logger logger = Logger.getLogger(TransformerDao.class);
	
	private ParameterizedRowMapper<Transformer> rowMapper;
	
	public TransformerDao()
	{
		this.rowMapper = new TransformerRowMapper();
	}

	@Override
	protected String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<Transformer> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(Transformer item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (label, description, jarPath, fullClassName) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getJarPath(),
			item.getFullClassName()
		};
		
		getJdbcTemplate().update(query, params);
	}
	
	@Override
	public void delete(Transformer item)
	{
		deleteRelatedPipelinesMapping(item.getId());
		deleteRaw(item.getId());
	}
	
	private void deleteRelatedPipelinesMapping(Long transformerId)
	{
		String query = "DELETE FROM " + TransformerInstanceDao.TABLE_NAME + " WHERE transformerId = ?";
		Object[] params = { transformerId };

		getJdbcTemplate().update(query, params);
	}
}
