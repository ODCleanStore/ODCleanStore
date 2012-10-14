package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRenameTemplateInstanceCompiler;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNReplaceTemplateInstanceCompiler;

public class DNReplaceTemplateInstanceDao extends DNTemplateInstanceDao<DNReplaceTemplateInstance>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = TABLE_NAME_PREFIX + "DN_REPLACE_TEMPLATE_INSTANCES";
	
	private ParameterizedRowMapper<DNReplaceTemplateInstance> rowMapper;

	public DNReplaceTemplateInstanceDao()
	{
		this.rowMapper = new DNReplaceTemplateInstanceRowMapper();
		this.compiler = new DNReplaceTemplateInstanceCompiler();
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
	public void save(final DNReplaceTemplateInstance item) throws Exception
	{
		executeInTransaction(new CodeSnippet() 
		{	
			@Override
			public void execute() throws Exception 
			{
				CompiledDNRule compiledRule = compiler.compile(item);
				int rawRuleId = saveCompiledRuleAndGetKey(compiledRule);
				item.setRawRuleId(rawRuleId);
				
				String query = 
					"INSERT INTO " + TABLE_NAME + " (groupId, rawRuleId, propertyName, pattern, replacement) " +
					"VALUES (?, ?, ?, ?, ?)";
				
				Object[] params =
				{
					item.getGroupId(),
					item.getRawRuleId(),
					item.getPropertyName(),
					item.getPattern(),
					item.getReplacement()
				};
		
				logger.debug("groupId: " + item.getGroupId());
				logger.debug("rawRuleId: " + item.getRawRuleId());
				logger.debug("propertyName: " + item.getPropertyName());
				logger.debug("pattern: " + item.getPattern());
				logger.debug("replacement: " + item.getReplacement());
				
				jdbcUpdate(query, params);
			}
		});
	}
	
	public void update(DNReplaceTemplateInstance item) throws Exception
	{
		String query =
			"UPDATE " + TABLE_NAME + " SET rawRuleId = ?, propertyName = ?, pattern = ?, replacement = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getRawRuleId(),
			item.getPropertyName(),
			item.getPattern(),
			item.getReplacement(),
			item.getId()
		};
		
		logger.debug("rawRuleId: " + item.getRawRuleId());
		logger.debug("propertyName: " + item.getPropertyName());
		logger.debug("pattern: " + item.getPattern());
		logger.debug("replacement: " + item.getReplacement());
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
