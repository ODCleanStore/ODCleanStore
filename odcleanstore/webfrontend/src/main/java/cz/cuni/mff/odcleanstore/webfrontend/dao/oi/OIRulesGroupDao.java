package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRulesGroup;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class OIRulesGroupDao extends DaoForEntityWithSurrogateKey<OIRulesGroup>
{
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "OI_RULES_GROUPS";

	private static final long serialVersionUID = 1L;
	
	private OIRulesGroupRowMapper rowMapper;
	
	public OIRulesGroupDao()
	{
		rowMapper = new OIRulesGroupRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<OIRulesGroup> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public void save(OIRulesGroup item) throws Exception
	{
		String query = "INSERT INTO " + TABLE_NAME + " (label, description) VALUES (?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		
		jdbcUpdate(query, params);
	}
	
	public void update(OIRulesGroup item) throws Exception
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
