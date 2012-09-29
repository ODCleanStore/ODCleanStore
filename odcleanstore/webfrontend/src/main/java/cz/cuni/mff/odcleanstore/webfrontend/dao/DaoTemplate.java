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
	 * Finds all entities in the database.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<T> loadAll()
	{
		String query = "SELECT * FROM " + getTableName();
		return jdbcQuery(query, getRowMapper());
	}
	
	public T loadFirst()
	{
		String query = "SELECT TOP 1 * FROM " + getTableName();
		return jdbcQueryForObject(query, getRowMapper());
	}
	
	/**
	 * 
	 * @param columnName
	 * @param value
	 * @return
	 */
	public List<T> loadAllBy(String columnName, Object value)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE " + columnName + " = ?";
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
			"SELECT * FROM " + getTableName() +
			criteria.buildWhereClause() +
			criteria.buildOrderByClause();
		
		Object[] params = criteria.buildWhereClauseParams();
		
		return jdbcQuery(query, params, getRowMapper());
	}
	
	public T loadBy(String columnName, Object value)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE " + columnName + " = ?";
		Object[] params = { value };
		
		logger.debug("value: " + value);
		
		return (T) jdbcQueryForObject(query, params, getRowMapper());
	}
}
