package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.RulesGroupEntity;
import cz.cuni.mff.odcleanstore.webfrontend.dao.users.UserDao;

public abstract class RulesGroupDao<T extends RulesGroupEntity> extends DaoForAuthorableEntity<T>
{
	private static final long serialVersionUID = 1L;
	
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

	@Override
	public int getAuthorId(Integer entityId)
	{
		return load(entityId).getAuthorId();
	}
}
