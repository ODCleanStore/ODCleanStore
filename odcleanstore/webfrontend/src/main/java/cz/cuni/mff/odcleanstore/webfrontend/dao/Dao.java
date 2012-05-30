package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.util.List;

/**
 * Generic DAO interface.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class Dao<T extends BusinessObject>
{
	public static final String TABLE_NAME_PREFIX = "DB.ODCLEANSTORE.";
	
	protected JdbcTemplate jdbcTemplate;
	
	/**
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * Finds all entities in the database.
	 * 
	 * @return
	 */
	public abstract List<T> loadAll();
	
	public List<T> loadAllRaw()
	{
		String query = "SELECT * FROM " + getTableName();
		return jdbcTemplate.query(query, getRowMapper());
	}
	
	/**
	 * Finds the entity with the given id in the database.
	 * 
	 * @param id
	 * @return
	 */
	public abstract T load(Long id);
	
	public T loadRaw(Long id)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE id = ?";
		Object[] params = { id };
		
		return (T) jdbcTemplate.queryForObject(query, params, getRowMapper());
	}
	
	/**
	 * Deletes the given item in the database.
	 * 
	 * @param item
	 */
	public void delete(T item)
	{
		throw new UnsupportedOperationException(
			"Cannot delete rows from table: " + getTableName() + "."
		);
	}
	
	public void deleteRaw(Long id)
	{
		String query = "DELETE FROM " + getTableName() + " WHERE id = ?";
		Object[] params = { id };
		
		jdbcTemplate.update(query, params);
	}
	
	/**
	 * Saves the given item in the database.
	 * 
	 * @param item
	 */
	public void save(T item)
	{
		throw new UnsupportedOperationException(
			"Cannot insert rows into table:" + getTableName() + "."
		);
	}
	
	/**
	 * Updates the given item in the database.
	 * 
	 * @param item
	 */
	public void update(T item)
	{
		throw new UnsupportedOperationException(
			"Cannot update rows in table: " + getTableName() + "."
		);
	}
	
	protected abstract String getTableName();
	
	protected abstract ParameterizedRowMapper<T> getRowMapper();
}
