package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DNRulesGroupDao extends DaoForEntityWithSurrogateKey<DNRulesGroup>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULES_GROUPS";

	private static final long serialVersionUID = 1L;
	
	private DNRulesGroupRowMapper rowMapper;
	
	public DNRulesGroupDao()
	{
		rowMapper = new DNRulesGroupRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNRulesGroup> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(DNRulesGroup item) throws Exception
	{
		String query = "INSERT INTO " + TABLE_NAME + " (label, description, authorId) VALUES (?, ?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getAuthorId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		
		jdbcUpdate(query, params);
	}

	public void update(DNRulesGroup item) throws Exception
	{
		String query = "UPDATE " + TABLE_NAME + " SET label = ?, description = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
}
