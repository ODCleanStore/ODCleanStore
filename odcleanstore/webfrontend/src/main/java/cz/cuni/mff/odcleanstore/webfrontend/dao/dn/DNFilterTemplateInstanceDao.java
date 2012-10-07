package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNFilterTemplateInstance;

public class DNFilterTemplateInstanceDao extends DNTemplateInstanceDao<DNFilterTemplateInstance>
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
	public void save(DNFilterTemplateInstance item) throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, rawRuleId, propertyName, pattern, keep) " +
			"VALUES (?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getRawRuleId(),
			item.getPropertyName(),
			item.getPattern(),
			item.getKeep()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("rawRuleId: " + item.getRawRuleId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("pattern: " + item.getPattern());
		logger.debug("keep: " + item.getKeep());
		
		jdbcUpdate(query, params);
	}
	
	public void update(DNFilterTemplateInstance item) throws Exception
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET rawRuleId = ?, propertyName = ?, pattern = ?, keep = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getRawRuleId(),
			item.getPropertyName(),
			item.getPattern(),
			item.getKeep(),
			item.getId()
		};
		
		logger.debug("rawRuleId: " + item.getRawRuleId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("pattern: " + item.getPattern());
		logger.debug("keep: " + item.getKeep());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}

	@Override
	public int getAuthorId(Integer entityId)
	{
		String query = "SELECT g.authorId " +
			"\n FROM " + TABLE_NAME + " AS t JOIN " + DNRulesGroupDao.TABLE_NAME + " AS g ON (g.id = t.groupId)" +
			"\n WHERE t.id = ?";
		return jdbcQueryForInt(query, entityId);
	}

}
