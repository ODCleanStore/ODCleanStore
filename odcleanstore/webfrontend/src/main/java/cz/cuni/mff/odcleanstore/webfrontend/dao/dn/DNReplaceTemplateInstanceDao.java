package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DNReplaceTemplateInstanceDao extends DaoForEntityWithSurrogateKey<DNReplaceTemplateInstance>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_REPLACE_TEMPLATE_INSTANCES";
	
	private ParameterizedRowMapper<DNReplaceTemplateInstance> rowMapper;

	public DNReplaceTemplateInstanceDao()
	{
		this.rowMapper = new DNReplaceTemplateInstanceRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNReplaceTemplateInstance> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public void save(DNReplaceTemplateInstance item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, rawRuleId, propertyName, pattern, replacement) " +
			"VALUES (?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			1,							// TODO: to be changed
			item.getPropertyName(),
			item.getPattern(),
			item.getReplacement()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("pattern: " + item.getPattern());
		logger.debug("replacement: " + item.getReplacement());
		
		getCleanJdbcTemplate().update(query, params);
	}
	
	@Override
	public void update(DNReplaceTemplateInstance item)
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET propertyName = ?, pattern = ?, replacement = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getPropertyName(),
			item.getPattern(),
			item.getReplacement(),
			item.getId()
		};
		
		logger.debug("groupId: " + item.getGroupId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("pattern: " + item.getPattern());
		logger.debug("replacement: " + item.getReplacement());
		logger.debug("id: " + item.getId());
		
		getCleanJdbcTemplate().update(query, params);
	}
}
