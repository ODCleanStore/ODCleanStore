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
	public String getTableName() 
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
	public void update(Transformer item)
	{
		// note that it is not possible to change the fullClassName of an existing transformer,
		// because the fullClassName parameter determines the way rules groups are added to
		// transformer instances and changing it could lead into a non consistent content of DB
		//
		String query = 
			"UPDATE " + TABLE_NAME + " SET label = ?, description = ?, jarPath = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getJarPath(),
			item.getId()
		};
		
		getJdbcTemplate().update(query, params);
	}
}
