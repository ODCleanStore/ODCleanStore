package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;

public abstract class AbstractRulesGroupDao<T extends RulesGroupEntity> extends DaoForAuthorableEntity<T>
{
	private static final long serialVersionUID = 1L;
	
	/** Suffix of tables for uncommitted rules. */
	public static final String UNCOMMITTED_TABLE_SUFFIX = "_UNCOMMITTED";
	
	@Override
	protected String getSelectAndFromClause()
	{
		String query = "SELECT u.username, g.* " +
			" FROM " + getTableName() + " AS g LEFT JOIN " + UserDao.TABLE_NAME + " AS u ON (g.authorId = u.id)";
		return query;
	}
	
	@Override
	public T load(Integer id)
	{
		return loadBy("g.id", id);
	}

	@Override
	public void save(T item) throws Exception
	{
		String query = "INSERT INTO " + getTableName() + " (label, description, authorId) VALUES (?, ?, ?)";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getAuthorId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		
		jdbcUpdate(query, params);
	}

	public void update(T item) throws Exception
	{
		String query = "UPDATE " + getTableName() + " SET label = ?, description = ? WHERE id = ?";
		
		Object[] params =
		{
			item.getLabel(),
			item.getDescription(),
			item.getId()
		};
		
		logger.debug("label: " + item.getLabel());
		logger.debug("description: " + item.getDescription());
		logger.debug("id: " + item.getId());
		
		jdbcUpdate(query, params);
	}
	
	public void markUncommitted(Integer groupId) throws Exception
	{
		setIsCommitted(groupId, true);
	}
	
	private void setIsCommitted(Integer groupId, boolean isUncommitted) throws Exception
	{
		String query  = "UPDATE " + getTableName() + " SET isUncommitted = ? WHERE id = ?";
		jdbcUpdate(query, boolToSmallint(isUncommitted), groupId);
	}
	
	protected abstract Class<? extends AbstractRuleDao<?>> getDependentRuleDao();
	
	public void commitChanges(final Integer groupId) throws Exception
	{
		AbstractRuleDao<?> uncommittedRuleDao = getLookupFactory().getDao(getDependentRuleDao(), true);
		uncommittedRuleDao.commitChanges(groupId);
		setIsCommitted(groupId, false);
	}

	@Override
	public int getAuthorId(Integer entityId)
	{
		return load(entityId).getAuthorId();
	}
}
