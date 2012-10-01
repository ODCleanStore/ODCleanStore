package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RulesGroupDao;

/**
 * 
 * @author Dusan
 *
 */
public class DNRuleComponentUncommittedDao extends DNRuleComponentDao
{
	public static final String TABLE_NAME = DNRuleComponentDao.TABLE_NAME + RulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected String getRuleTableName()
	{
		return DNRuleUncommittedDao.TABLE_NAME;
	}
	
	private int getGroupId(Integer ruleId)
	{
		return getLookupFactory().getDao(DNRuleDao.class).load(ruleId).getGroupId();
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(load(id).getRuleId());
		getLookupFactory().getDao(DNRulesGroupDao.class).setUncommitted(groupId);
				
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}
	
	@Override
	public void save(DNRuleComponent item) throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(item.getRuleId());
		getLookupFactory().getDao(DNRulesGroupDao.class).setUncommitted(groupId);
				
		String query = 
			"INSERT INTO " + getTableName() + " (ruleId, typeId, modification, description) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getRuleId(),
			item.getType().getId(),
			item.getModification(),
			item.getDescription()
		};
		
		logger.debug("ruleId: " + item.getRuleId());
		logger.debug("typeId: " + item.getType().getId());
		logger.debug("modification: " + item.getModification());
		logger.debug("description: " + item.getDescription());
		
		jdbcUpdate(query, params);
	}

	public void update(DNRuleComponent item) throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(item.getRuleId());
		getLookupFactory().getDao(DNRulesGroupDao.class).setUncommitted(groupId);
				
		String query = 
			"UPDATE " + getTableName() + " " +
			"SET typeId = ?, modification = ?, description = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getType().getId(),
			item.getModification(),
			item.getDescription(),
			item.getId()
		};
		
		logger.debug("typeId: " + item.getType().getId());
		logger.debug("description: " + item.getModification());
		logger.debug("description: " + item.getDescription());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
}
