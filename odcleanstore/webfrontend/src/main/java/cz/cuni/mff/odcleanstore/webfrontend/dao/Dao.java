package cz.cuni.mff.odcleanstore.webfrontend.dao;

import cz.cuni.mff.odcleanstore.webfrontend.bo.BusinessObject;
import cz.cuni.mff.odcleanstore.webfrontend.core.DaoLookupFactory;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * Generic DAO interface.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class Dao<T extends BusinessObject> implements Serializable
{
	public static final String TABLE_NAME_PREFIX = "DB.ODCLEANSTORE.";
	
	private static final long serialVersionUID = 1L;
		
	private static Logger logger = Logger.getLogger(Dao.class);
	
	private DaoLookupFactory lookupFactory;
	
	private transient JdbcTemplate jdbcTemplate;
	private transient TransactionTemplate transactionTemplate;
	
	public void setDaoLookupFactory(DaoLookupFactory lookupFactory)
	{
		this.lookupFactory = lookupFactory;
	}
	
	/**
	 * 
	 * @return
	 */
	protected JdbcTemplate getJdbcTemplate()
	{
		if (jdbcTemplate == null)
		{
			DataSource dataSource = lookupFactory.getDataSource();
			jdbcTemplate = new JdbcTemplate(dataSource);
		}
		
		return jdbcTemplate;
	}
	
	/**
	 * 
	 * @return
	 */
	protected TransactionTemplate getTransactionTemplate()
	{
		if (transactionTemplate == null)
		{
			AbstractPlatformTransactionManager manager = lookupFactory.getTransactionManager();
			transactionTemplate = new TransactionTemplate(manager);
			transactionTemplate.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
		}
		
		return transactionTemplate;
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
		return getJdbcTemplate().query(query, getRowMapper());
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
		
		return (T) getJdbcTemplate().queryForObject(query, params, getRowMapper());
	}
	
	public T loadRawBy(String columnName, String value)
	{
		String query = "SELECT * FROM " + getTableName() + " WHERE " + columnName + " = ?";
		Object[] params = { value };
		
		return (T) getJdbcTemplate().queryForObject(query, params, getRowMapper());
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
		
		getJdbcTemplate().update(query, params);
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
