package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DNFilterTemplateInstanceDao extends DaoForEntityWithSurrogateKey<DNFilterTemplateInstance>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_FILTER_TEMPLATE_INSTANCES";
	
	private ParameterizedRowMapper<DNFilterTemplateInstance> rowMapper;

	public DNFilterTemplateInstanceDao()
	{
		this.rowMapper = new DNFilterTemplateInstanceRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNFilterTemplateInstance> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public void save(DNFilterTemplateInstance item) 
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, rawRuleId, propertyName, pattern, keep) " +
			"VALUES (?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			1,							// TODO: to be changed
			item.getPropertyName(),
			item.getPattern(),
			item.getKeep()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("pattern: " + item.getPattern());
		logger.debug("keep: " + item.getKeep());
		
		getCleanJdbcTemplate().update(query, params);
	}
	
	@Override
	public void update(DNFilterTemplateInstance item)
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET propertyName = ?, pattern = ?, keep = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getPropertyName(),
			item.getPattern(),
			item.getKeep(),
			item.getId()
		};
		
		logger.debug("groupId: " + item.getGroupId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("pattern: " + item.getPattern());
		logger.debug("keep: " + item.getKeep());
		logger.debug("id: " + item.getId());
		
		getCleanJdbcTemplate().update(query, params);
	}
}
