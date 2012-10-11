package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNConcatenateTemplateInstance;

public class DNConcatenateTemplateInstanceDao extends DNTemplateInstanceDao<DNConcatenateTemplateInstance> {
	private static final long serialVersionUID = 1L;
	
	public static String TABLE_NAME = TABLE_NAME_PREFIX + "DN_CONCATENATE_TEMPLATE_INSTANCES";

	private ParameterizedRowMapper<DNConcatenateTemplateInstance> rowMapper;

	public DNConcatenateTemplateInstanceDao()
	{
		this.rowMapper = new DNConcatenateTemplateInstanceRowMapper();
	}
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<DNConcatenateTemplateInstance> getRowMapper() 
	{
		return rowMapper;
	}

	@Override
	public void save(DNConcatenateTemplateInstance item) throws Exception
	{
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, rawRuleId, propertyName, delimiter) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getRawRuleId(),
			item.getPropertyName(),
			item.getDelimiter()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("rawRuleId: " + item.getRawRuleId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("delimiter: '" + item.getDelimiter() + "'");
		
		jdbcUpdate(query, params);
	}
	
	public void update(DNConcatenateTemplateInstance item) throws Exception
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET rawRuleId = ?, propertyName = ?, delimiter = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getRawRuleId(),
			item.getPropertyName(),
			item.getDelimiter(),
			item.getId()
		};
		
		logger.debug("rawRuleId: " + item.getRawRuleId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("delimiter:" + item.getDelimiter());
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
