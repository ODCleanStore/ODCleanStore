package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

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
	
	private static Logger logger = Logger.getLogger(Dao.class);
	
	protected JdbcTemplate jdbcTemplate;
	protected TransactionTemplate transactionTemplate;
	
	/**
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource)
	{
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	/**
	 * 
	 * @param transactionManager
	 */
	public void setTransactionManager(AbstractPlatformTransactionManager transactionManager)
	{
		this.transactionTemplate = new TransactionTemplate(transactionManager);
		this.transactionTemplate.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
	}
	
	/**
	 * Finds all entities in the database.
	 * 
	 * @return
	 * @throws Exception 
	 */
	public List<T> loadAll()
	{
		return loadAllRaw();
	}
	
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
	public T load(Long id)
	{
		return loadRaw(id);
	}
	
	public T loadRaw(Long id)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE id = ?";
		Object[] params = { id };
		
		return (T) jdbcTemplate.queryForObject(query, params, getRowMapper());
	}
	
	public T loadRawBy(String columnName, String value)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE " + columnName + " = ?";
		Object[] params = { value };
		
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
	public void save(T item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot insert rows into table:" + getTableName() + "."
		);
	}
	
	/**
	 * Updates the given item in the database.
	 * 
	 * @param item
	 * @throws Exception 
	 */
	public void update(T item) throws Exception
	{
		throw new UnsupportedOperationException(
			"Cannot update rows in table: " + getTableName() + "."
		);
	}
	
	protected abstract String getTableName();
	
	protected abstract ParameterizedRowMapper<T> getRowMapper();
}
