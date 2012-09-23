package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DNRuleDao extends DaoForEntityWithSurrogateKey<DNRule>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RULES";
	
	private ParameterizedRowMapper<DNRule> rowMapper;
	
	public DNRuleDao()
	{
		this.rowMapper = new DNRuleRowMapper();
	}

	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNRule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public void save(DNRule item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, description) " +
			"VALUES (?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getDescription()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("description: " + item.getDescription());
		
		getCleanJdbcTemplate().update(query, params);
	}
	
	@Override
	public void update(DNRule item)
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET description = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getDescription(),
			item.getId()
		};
		
		logger.debug("description: " + item.getDescription());
		logger.debug("id: " + item.getId());
		
		getCleanJdbcTemplate().update(query, params);
	}
}
