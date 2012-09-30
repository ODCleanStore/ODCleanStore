package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;

public class DNRenameTemplateInstanceDao extends DaoForEntityWithSurrogateKey<DNRenameTemplateInstance>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RENAME_TEMPLATE_INSTANCES";
	
	private ParameterizedRowMapper<DNRenameTemplateInstance> rowMapper;

	public DNRenameTemplateInstanceDao()
	{
		this.rowMapper = new DNRenameTemplateInstanceRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNRenameTemplateInstance> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public void save(DNRenameTemplateInstance item) throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, rawRuleId, sourcePropertyName, targetPropertyName) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			1,							// TODO: to be changed
			item.getSourcePropertyName(),
			item.getTargetPropertyName(),
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("sourcePropertyName: " + item.getSourcePropertyName());
		logger.debug("targetPropertyName: " + item.getTargetPropertyName());
		
		jdbcUpdate(query, params);
	}
	
	public void update(DNRenameTemplateInstance item) throws Exception
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET sourcePropertyName = ?, targetPropertyName = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getSourcePropertyName(),
			item.getTargetPropertyName(),
			item.getId()
		};
		
		logger.debug("groupId: " + item.getGroupId());
		logger.debug("sourcePropertyName: " + item.getSourcePropertyName());
		logger.debug("targetPropertyName: " + item.getTargetPropertyName());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
}
