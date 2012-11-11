package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.CompiledDNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNTemplateInstance;
import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNTemplateInstanceCompiler;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;
import cz.cuni.mff.odcleanstore.webfrontend.dao.DaoForAuthorableEntity;
import cz.cuni.mff.odcleanstore.webfrontend.dao.SimpleKeyHolder;
import cz.cuni.mff.odcleanstore.webfrontend.util.CodeSnippet;

/**
 * An abstract parent of all DN template instance DAOs.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 * @param <BO>
 */
public abstract class DNTemplateInstanceDao<BO extends DNTemplateInstance> extends DaoForAuthorableEntity<BO>
{
	private static final long serialVersionUID = 1L;

	protected DNTemplateInstanceCompiler<BO> compiler;
	
	private DNRuleDao dnRuleDao;
	
	@Override
	public void setDaoLookupFactory(DaoLookupFactory factory)
	{
		super.setDaoLookupFactory(factory);
		
		this.dnRuleDao = factory.getDao(DNRuleUncommittedDao.class);
	}
	
	@Override
	protected void deleteRaw(final Integer id) throws Exception
	{
		executeInTransaction(new CodeSnippet()
		{
			@Override
			public void execute() throws Exception
			{
				DNTemplateInstance templateInstance = load(id);
				
				// Mark the group as dirty
				getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(templateInstance.getGroupId());
				
				DNTemplateInstanceDao.super.deleteRaw(id);
				
				getLookupFactory().getDao(DNRuleDao.class, true).delete(templateInstance.getRawRuleId());
			}
		});
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 */
	protected int saveCompiledRuleAndGetKey(final CompiledDNRule item) throws Exception
	{
		final SimpleKeyHolder keyHolder = new SimpleKeyHolder();
		
		executeInTransaction(new CodeSnippet() 
		{	
			@Override
			public void execute() throws Exception 
			{
				// Mark the group as dirty
				getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(item.getGroupId());
				
				Integer ruleId = saveRawRuleAndGetKey(item);
				keyHolder.setKey(ruleId);
				
				for (CompiledDNRuleComponent component : item.getComponents())
					saveRawComponent(ruleId, component);
			}
		});
		
		return keyHolder.getKey();
	}
	
	/**
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 */
	private int saveRawRuleAndGetKey(final CompiledDNRule item) throws Exception
	{
		DNRule rule = new DNRule(item.getGroupId(), item.getLabel(), item.getDescription());
		return dnRuleDao.saveAndGetKey(rule);
	}
	
	/**
	 * 
	 * @param ruleId
	 * @param item
	 * @throws Exception
	 */
	private void saveRawComponent(Integer ruleId, CompiledDNRuleComponent item) throws Exception
	{
		int typeId = getComponentTypeId(item.getTypeLabel().toString());

		String query =
			"INSERT INTO " + DNRuleComponentUncommittedDao.TABLE_NAME + " " + 
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
	
	/**
	 * 
	 * @param componentTypeLabel
	 * @return
	 */
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
