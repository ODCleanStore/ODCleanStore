package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.util.CodeSnippet;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForEntityWithSurrogateKey;
import cz.cuni.mff.odcleanstore.webfrontend.dao.SimpleKeyHolder;

public class CompiledDNRuleDao extends DaoForEntityWithSurrogateKey<CompiledDNRule>
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = DNRuleDao.TABLE_NAME;
	
	private ParameterizedRowMapper<CompiledDNRule> rowMapper;
	
	public CompiledDNRuleDao()
	{
		this.rowMapper = new CompiledDNRuleRowMapper();
	}

	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}

	@Override
	protected ParameterizedRowMapper<CompiledDNRule> getRowMapper() 
	{
		return rowMapper;
	}
	
	@Override
	public int saveAndGetKey(final CompiledDNRule item) throws Exception
	{
		final SimpleKeyHolder keyHolder = new SimpleKeyHolder();
		
		executeInTransaction(new CodeSnippet() 
		{	
			@Override
			public void execute() throws Exception 
			{
				saveRawRule(item);
				
				Integer ruleId = getLastInsertId();
				keyHolder.setKey(ruleId);
				
				for (CompiledDNRuleComponent component : item.getComponents())
					saveRawComponent(ruleId, component);
			}
		});
		
		return keyHolder.getKey();
	}
	
	private void saveRawRule(CompiledDNRule item) throws Exception
	{
		String query = 
			"INSERT INTO " + DNRuleDao.TABLE_NAME + " (groupId, description) " +
			"VALUES (?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getDescription()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("description: " + item.getDescription());
		
		jdbcUpdate(query, params);
	}
	
	private void saveRawComponent(Integer ruleId, CompiledDNRuleComponent item) 
		throws Exception
	{
		int typeId = getComponentTypeId(item.getTypeLabel().toString());
		
		String query =
			"INSERT INTO " + DNRuleComponentDao.TABLE_NAME + " " + 
			"(ruleId, typeId, modification, description) " +
			"VALUES (?, ?, ?, ?)";
	
		Object[] params =
		{
			ruleId,
			typeId,
			item.getModification(),
			item.getDescription()
		};
		
		logger.debug("ruleId: " + ruleId);
		logger.debug("typeId: " + typeId);
		logger.debug("modification: " + item.getModification());
		logger.debug("description: " + item.getDescription());
		
		jdbcUpdate(query, params);
	}
	
	private int getComponentTypeId(String componentTypeLabel)
	{
		String query = 
			"SELECT id FROM " + DNRuleComponentTypeDao.TABLE_NAME + " " +
			"WHERE label = ?";
		
		Object[] params = { componentTypeLabel };
		
		logger.debug("type label: " + componentTypeLabel);
		
		return jdbcQueryForInt(query, params);
	}
}
