package cz.cuni.mff.odcleanstore.webfrontend.dao.oi;

import cz.cuni.mff.odcleanstore.webfrontend.bo.oi.OIRule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RulesGroupDao;

public class OIRuleUncommittedDao extends OIRuleDao
{
	public static final String TABLE_NAME = OIRuleDao.TABLE_NAME + RulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}

	@Override
	public void save(OIRule item) throws Exception
	{
		String query = 
			"INSERT INTO " + getTableName() + " " +
			"(groupId, label, linkType, sourceRestriction, targetRestriction, linkageRule, filterThreshold, filterLimit) " +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getLabel(),
			item.getLinkType(),
			item.getSourceRestriction(),
			item.getTargetRestriction(),
			item.getLinkageRule(),
			item.getFilterThreshold(),
			item.getFilterLimit()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("label: " + item.getLabel());
		logger.debug("linkType: " + item.getLinkType());
		logger.debug("sourceRestriction: " + item.getSourceRestriction());
		logger.debug("targetRestriction: " + item.getTargetRestriction());
		logger.debug("linkageRule: " + item.getLinkageRule());
		logger.debug("filterThreshold: " + item.getFilterThreshold());
		logger.debug("filterLimit: " + item.getFilterLimit());
		
		jdbcUpdate(query, params);
	}
	
	public void update(OIRule item) throws Exception
	{
		String query =
			"UPDATE " + getTableName() + 
			" SET label = ?, linkType = ?, sourceRestriction = ?, targetRestriction = ?, linkageRule = ?, filterThreshold = ?, filterLimit = ? " +
			" WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getLinkType(),
			item.getSourceRestriction(),
			item.getTargetRestriction(),
			item.getLinkageRule(),
			item.getFilterThreshold(),
			item.getFilterLimit(),
			item.getId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("linkType: " + item.getLinkType());
		logger.debug("sourceRestriction: " + item.getSourceRestriction());
		logger.debug("targetRestriction: " + item.getTargetRestriction());
		logger.debug("linkageRule: " + item.getLinkageRule());
		logger.debug("filterThreshold: " + item.getFilterThreshold());
		logger.debug("filterLimit: " + item.getFilterLimit());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
}
