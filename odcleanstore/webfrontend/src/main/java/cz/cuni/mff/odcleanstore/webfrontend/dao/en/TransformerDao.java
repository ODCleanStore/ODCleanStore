package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;;

public class TransformerDao extends DaoForEntityWithSurrogateKey<Transformer> 
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "TRANSFORMERS";

	private static final long serialVersionUID = 1L;
	
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
}
