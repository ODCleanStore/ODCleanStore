package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.AbstractRulesGroupDao;

public class QARuleUncommittedDao extends QARuleDao
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = QARuleDao.TABLE_NAME + AbstractRulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

	@Override
	public String getTableName() 
	{
		return TABLE_NAME;
	}
	
	@Override
	protected void deleteRaw(Integer id) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(QARulesGroupDao.class).markUncommitted(load(id).getGroupId());
		
		String query = "DELETE FROM " + getTableName() + " WHERE " + KEY_COLUMN +" = ?";
		jdbcUpdate(query, id);
	}
	
	@Override
	public void save(QARule item) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(QARulesGroupDao.class).markUncommitted(item.getGroupId());
		
		String query = 
			"INSERT INTO " + getTableName() + " (groupId, filter, description, coefficient) " +
			"VALUES (?, ?, ?, ?)";
		
		Object[] params =
		{
			item.getGroupId(),
			item.getFilter(),
			item.getDescription(),
			item.getCoefficient()
		};

		logger.debug("groupId: " + item.getGroupId());
		logger.debug("filter: " + item.getFilter());
		logger.debug("description: " + item.getDescription());
		logger.debug("coefficient: " + item.getCoefficient());
		
		jdbcUpdate(query, params);
	}
	
	public void update(QARule item) throws Exception
	{
		// Mark the group as dirty
		getLookupFactory().getDao(QARulesGroupDao.class).markUncommitted(item.getGroupId());
		
		String query =
			"UPDATE " + getTableName() + " SET filter = ?, description = ?, coefficient = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getFilter(),
			item.getDescription(),
			item.getCoefficient(),
			item.getId()
		};
		
		logger.debug("filter: " + item.getFilter());
		logger.debug("description: " + item.getDescription());
		logger.debug("coefficient: " + item.getCoefficient());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
	
	@Override
	protected void commitChangesImpl(Integer groupId) throws Exception
	{
		copyBetweenTablesBy(getTableName(), super.getTableName(), GROUP_ID_COLUMN, groupId);
	}
}
