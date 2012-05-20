package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * Generic DAO interface.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class Dao<T extends BusinessObject>
{
	protected static final String TABLE_NAME_PREFIX = "DB.FRONTEND.";
	
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
	 * Deletes the given item in the database.
	 * 
	 * @param item
	 */
	public abstract void delete(T item);
	
	/**
	 * Saves the given item in the database.
	 * 
	 * @param item
	 */
	public abstract void save(T item);
	
	/**
	 * Updates the given item in the database.
	 * 
	 * @param item
	 */
	public abstract void update(T item);
	
	/**
	 * Finds all entities in the database.
	 * 
	 * @return
	 */
	public abstract List<T> loadAll();
	
	/**
	 * Finds the entity with the given id in the database.
	 * 
	 * @param id
	 * @return
	 */
	public abstract T load(Long id);
}
