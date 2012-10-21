package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

/**
 * Utility base class for DAOs, implementing common methods for loading of business entities.
 * 
 * Child classes can override the following methods to customize behavior of the loading methods:
 * <ul>
 * 	<li>{@link #getSelectAndFromClause()}</li>
 *  <li>{@link #postLoadAllBy(List)()}</li>
 *  <li>{@link #postLoadBy(BusinessEntity)}</li>
 * </ul>
 * 
 * @author Jan Michelfeit
 * @param <T> type of business entity
 */
public abstract class DaoTemplate<T extends BusinessEntity> extends Dao
{
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @return
	 */
	public abstract String getTableName();
	
	/**
	 * 
	 * @return
	 */
	protected abstract ParameterizedRowMapper<T> getRowMapper();
	
	/**
	 * Builds SELECT ... FROM ... part of the query.
	 * NOTE: When redefining this method for join of multiple tables, descendants
	 * of {@link DaoForEntityWithSurrogateKey} should redefine {@link DaoForEntityWithSurrogateKey#load(Integer)}
	 * to use unambiguous key column.
	 */
	protected String getSelectAndFromClause()
	{
		return "SELECT * FROM " + getTableName();
	}
	
	/**
	 * Finds all entities in the database.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public final List<T> loadAll()
	{
		String query = getSelectAndFromClause();
		return postLoadAllBy(jdbcQuery(query, getRowMapper()));
	}
	
	/**
	 * 
	 * @param columnName
	 * @param value
	 * @return
	 */
	public final List<T> loadAllBy(String columnName, Object value)
	{
		String query = getSelectAndFromClause() + " WHERE " + columnName + " = ?";
		Object[] params = { value };
		
		logger.debug("value: " + value);
		
		return postLoadAllBy(jdbcQuery(query, params, getRowMapper()));
	}
	
	/**
	 * 
	 * @param criteria
	 * @return
	 */
	public final List<T> loadAllBy(QueryCriteria criteria)
	{
		String query = 
			getSelectAndFromClause() +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();
		
		Object[] params = criteria.buildWhereClauseParams();
		
		return postLoadAllBy(jdbcQuery(query, params, getRowMapper()));
	}
	
	/**
	 * Method called after list of items is loaded.
	 * Override this method in order to perform additional actions (such as additional data loading). 
	 * loadAll* methods will then return value modified by this method.
	 * @param items items as loaded from database
	 * @return modified values
	 */
	protected List<T> postLoadAllBy(List<T> items) {
		return items;
	}
	
	public final T loadBy(String columnName, Object value)
	{
		String selectAndFromClause = getSelectAndFromClause();
		StringBuilder query = new StringBuilder();
		if (selectAndFromClause.startsWith("SELECT") || selectAndFromClause.startsWith("select")) 
		{
			query.append("SELECT TOP 1 ");
			query.append(selectAndFromClause.substring("SELECT".length()));
		} 
		else 
		{
			query.append(selectAndFromClause);
		}
		query.append(" WHERE ");
		query.append(columnName);
		query.append(" = ?");
		
		Object[] params = { value };
		
		logger.debug("value: " + value);
		
		T result = (T) jdbcQueryForObject(query.toString(), params, getRowMapper()); 
		return postLoadBy(result);
	}
	
	/**
	 * Method called after a single item is loaded.
	 * Override this method in order to perform additional actions (such as additional data loading). 
	 * {@link #loadBy(String, Object)} will then return value modified by this method.
	 * @param item item as loaded from database
	 * @return modified value
	 * @see #loadBy(String, Object)
	 */
	protected T postLoadBy(T item) {
		return item;
	}
	
	protected final void copyBetweenTablesBy(String fromTable, String toTable, String byColumn, Object byValue) throws Exception
	{
		String deleteQuery = "DELETE FROM " + toTable + " WHERE " + byColumn + " = ?";
		jdbcUpdate(deleteQuery, byValue);
		
		String insertQuery = "INSERT INTO " + toTable +
			" SELECT * FROM " + fromTable + " WHERE " + byColumn + " = ?";
		jdbcUpdate(insertQuery, byValue);
	}
}
