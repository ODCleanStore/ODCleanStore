package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstanceCompiler;

public class DNRenameTemplateInstanceDao extends DNTemplateInstanceDao<DNRenameTemplateInstance>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_RENAME_TEMPLATE_INSTANCES";
	
	private ParameterizedRowMapper<DNRenameTemplateInstance> rowMapper;

	public DNRenameTemplateInstanceDao()
	{
		this.rowMapper = new DNRenameTemplateInstanceRowMapper();
		this.compiler = new DNRenameTemplateInstanceCompiler();
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
		CompiledDNRule compiledRule = compiler.compile(item);
		int rawRuleId = saveCompiledRuleAndGetKey(compiledRule);
		item.setRawRuleId(rawRuleId);
		
		String query = 
			"INSERT INTO " + TABLE_NAME + " (groupId, rawRuleId, sourcePropertyName, targetPropertyName) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getRawRuleId(),
			item.getSourcePropertyName(),
			item.getTargetPropertyName(),
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("rawRuleId: " + item.getRawRuleId());
		logger.debug("sourcePropertyName: " + item.getSourcePropertyName());
		logger.debug("targetPropertyName: " + item.getTargetPropertyName());
		
		jdbcUpdate(query, params);
	}
	
	public void update(DNRenameTemplateInstance item) throws Exception
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET rawRuleId = ?, sourcePropertyName = ?, targetPropertyName = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getRawRuleId(),
			item.getSourcePropertyName(),
			item.getTargetPropertyName(),
			item.getId()
		};
		
		logger.debug("rawRuleId: " + item.getRawRuleId());
		logger.debug("sourcePropertyName: " + item.getSourcePropertyName());
		logger.debug("targetPropertyName: " + item.getTargetPropertyName());
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
