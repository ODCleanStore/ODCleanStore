package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessEntity;

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
	public List<T> loadAll()
	{
		String query = getSelectAndFromClause();
		return jdbcQuery(query, getRowMapper());
	}
	
	/**
	 * 
	 * @param columnName
	 * @param value
	 * @return
	 */
	public List<T> loadAllBy(String columnName, Object value)
	{
		String query = getSelectAndFromClause() + " WHERE " + columnName + " = ?";
		Object[] params = { value };
		
		logger.debug("value: " + value);
		
		return jdbcQuery(query, params, getRowMapper());
	}
	
	/**
	 * 
	 * @param criteria
	 * @return
	 */
	public List<T> loadAllBy(QueryCriteria criteria)
	{
		String query = 
			getSelectAndFromClause() +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();
		
		Object[] params = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, params, getRowMapper());
	}
	
	protected T loadBy(String columnName, Object value)
	{
		String selectAndFromClause = getSelectAndFromClause();
		if (selectAndFromClause.startsWith("SELECT")) 
		{
			selectAndFromClause = "SELECT TOP 1 " + selectAndFromClause.substring("SELECT".length());
		}
		String query = selectAndFromClause + " WHERE " + columnName + " = ?";
		
		Object[] params = { value };
		
		logger.debug("value: " + value);
		
		return (T) jdbcQueryForObject(query, params, getRowMapper());
	}
}
