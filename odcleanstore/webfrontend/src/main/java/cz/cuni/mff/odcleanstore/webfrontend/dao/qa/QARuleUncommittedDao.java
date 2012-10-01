package cz.cuni.mff.odcleanstore.webfrontend.dao.qa;

import cz.cuni.mff.odcleanstore.webfrontend.bo.qa.QARule;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RulesGroupDao;

public class QARuleUncommittedDao extends QARuleDao
{
	private static final long serialVersionUID = 1L;
	
	public static final String TABLE_NAME = QARuleDao.TABLE_NAME + RulesGroupDao.UNCOMMITTED_TABLE_SUFFIX;

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
	public void save(QARule item) throws Exception
	{
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
}
