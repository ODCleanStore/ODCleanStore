package cz.cuni.mff.odcleanstore.webfrontend.dao.dn;

import cz.cuni.mff.odcleanstore.webfrontend.bo.dn.DNRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRulesGroupDao;

public class DNRuleUncommittedDao extends DNRuleDao
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = DNRuleDao.TABLE_NAME + AbstractRulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(load(id).getGroupId());
				
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}

	@Override
	public void save(DNRule item) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(item.getGroupId());
				
		String query = 
			"INSERT INTO " + getTableName() + " (groupId, description) " +
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
	
	public void update(DNRule item) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(DNRulesGroupDao.class).markUncommitted(item.getGroupId());
				
		String query =
			"UPDATE " + getTableName() + " SET description = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getDescription(),
			item.getId()
		};
		
		logger.debug("description: " + item.getDescription());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
	
	@Override
	protected void commitChangesImpl(Integer groupId) throws Exception
	{
		// Delete old rules and inserts new rules to the official table
		// Also takes care of deleting in dependent tables via ON DELETE CASCADE 
		copyBetweenTablesBy(getTableName(), super.getTableName(), GROUP_ID_COLUMN, groupId);
		getLookupFactory().getDao(DNRuleComponentUncommittedDao.class).copyToOfficialTable(groupId);
	}
}
