package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRuleComponent;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRulesGroupDao;

/**
 * The DN rule component type for uncommitted DN rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DNRuleComponentUncommittedDao extends DNRuleComponentDao
{
	public static final String TABLE_NAME = DNRuleComponentDao.TABLE_NAME + AbstractRulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

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
	
	/**
	 * 
	 * @param ruleId
	 * @return
	 */
	private int getGroupId(Integer ruleId)
	{
		return getLookupFactory().getDao(DNRuleDao.class, true).load(ruleId).getGroupId();
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(load(id).getRuleId());
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(groupId);
				
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}
	
	@Override
	public void save(DNRuleComponent item) throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(item.getRuleId());
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(groupId);
				
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

	/**
	 * 
	 */
	public void update(DNRuleComponent item) throws Exception
	{
		// Mark the group as dirty
		Integer groupId = getGroupId(item.getRuleId());
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(groupId);
				
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
	
	/**
	 * 
	 * @param groupId
	 * @throws Exception
	 */
	/*package*/void copyToOfficialTable(Integer groupId) throws Exception
	{
		String insertQuery = "INSERT INTO " + super.getTableName() +
			" SELECT c.* " + 
			" FROM " + this.getTableName() + " AS c JOIN " + DNRuleUncommittedDao.TABLE_NAME + " AS r ON (c.ruleId = r.id)" +
			" WHERE r.groupId = ?";
		jdbcUpdate(insertQuery, groupId);
	}
}
