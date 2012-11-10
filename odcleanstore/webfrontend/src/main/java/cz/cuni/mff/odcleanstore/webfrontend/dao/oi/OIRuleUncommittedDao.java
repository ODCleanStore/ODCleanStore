package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRulesGroupDao;

/**
 * The OI rule DAO for uncommitted OI rules.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OIRuleUncommittedDao extends OIRuleDao
{
	public static final String TABLE_NAME = OIRuleDao.TABLE_NAME + AbstractRulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(OIRulesGroupDao.class).markUncommitted(load(id).getGroupId());
		
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}

	@Override
	public void save(OIRule item) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(OIRulesGroupDao.class).markUncommitted(item.getGroupId());
		
		String query = 
			"INSERT INTO " + getTableName() + " " +
			"(groupId, label, description, linkType, sourceRestriction, targetRestriction, linkageRule, filterThreshold, filterLimit) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getLabel(),
			item.getDescription(),
			item.getLinkType(),
			item.getSourceRestriction(),
			item.getTargetRestriction(),
			item.getLinkageRule(),
			item.getFilterThreshold(),
			item.getFilterLimit()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		logger.debug("linkType: " + item.getLinkType());
		logger.debug("sourceRestriction: " + item.getSourceRestriction());
		logger.debug("targetRestriction: " + item.getTargetRestriction());
		logger.debug("linkageRule: " + item.getLinkageRule());
		logger.debug("filterThreshold: " + item.getFilterThreshold());
		logger.debug("filterLimit: " + item.getFilterLimit());
		
		jdbcUpdate(query, params);
	}
	
	/**
	 * 
	 * @param item
	 */
	public void update(OIRule item) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(OIRulesGroupDao.class).markUncommitted(item.getGroupId());
		
		String query =
			"UPDATE " + getTableName() + 
			" SET label = ?, description = ?, linkType = ?, sourceRestriction = ?, targetRestriction = ?, linkageRule = ?, filterThreshold = ?, filterLimit = ? " +
			" WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getLinkType(),
			item.getSourceRestriction(),
			item.getTargetRestriction(),
			item.getLinkageRule(),
			item.getFilterThreshold(),
			item.getFilterLimit(),
			item.getId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		logger.debug("linkType: " + item.getLinkType());
		logger.debug("sourceRestriction: " + item.getSourceRestriction());
		logger.debug("targetRestriction: " + item.getTargetRestriction());
		logger.debug("linkageRule: " + item.getLinkageRule());
		logger.debug("filterThreshold: " + item.getFilterThreshold());
		logger.debug("filterLimit: " + item.getFilterLimit());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
	
	@Override
	protected void commitChangesImpl(Integer groupId) throws Exception
	{
		// Delete old rules and inserts new rules to the official table
		// Also takes care of deleting in dependent tables via ON DELETE CASCADE 
		copyBetweenTablesBy(getTableName(), super.getTableName(), GROUP_ID_COLUMN, groupId);
		getLookupFactory().getDao(OIOutputUncommittedDao.class).copyToOfficialTable(groupId);
	}
}
