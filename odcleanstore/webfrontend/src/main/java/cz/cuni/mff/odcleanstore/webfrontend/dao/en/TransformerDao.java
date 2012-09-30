package cz.cuni.mff.odcleanstore.webfrontend.dao.en;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.en.Transformer;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

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
	public void save(Transformer item) throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (label, description, jarPath, workDirPath, fullClassName) " +
			"VALUES (?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getJarPath(),
			item.getWorkDirPath(),
			item.getFullClassName()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		logger.debug("jar path: " + item.getJarPath());
		logger.debug("work dir path: " + item.getWorkDirPath());
		logger.debug("full classname: " + item.getFullClassName());
		
		jdbcUpdate(query, params);
	}
	
	public void update(Transformer item) throws Exception
	{
		// note that it is not possible to change the fullClassName of an existing transformer,
		// because the fullClassName parameter determines the way rules groups are added to
		// transformer instances and changing it could lead into a non consistent content of DB
		//
		String query = 
			"UPDATE " + TABLE_NAME + " SET label = ?, description = ?, jarPath = ?, workDirPath = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getJarPath(),
			item.getWorkDirPath(),
			item.getId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		logger.debug("jar path: " + item.getJarPath());
		logger.debug("work dir path: " + item.getWorkDirPath());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
}
