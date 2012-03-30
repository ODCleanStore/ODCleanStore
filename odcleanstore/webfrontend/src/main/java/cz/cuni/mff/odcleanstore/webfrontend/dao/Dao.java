package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Date;

import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Generic DAO interface.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class Dao<T>
{
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
	 * Insert the given item into the database.
	 * 
	 * @param item
	 */
	public abstract void insert(T item);
	
	/**
	 * Updates the given entity in the database.
	 * 
	 * @param item
	 */
	public abstract void update(T item);
	
	/**
	 * Find all entities in the database.
	 * 
	 * @return
	 */
	public abstract List<T> loadAll();
	
	/**
	 * Find the entity with the given id in the database.
	 * 
	 * @param id
	 * @return
	 */
	public abstract T load(int id);
	
	/**
	 * Converts the given Date instance to a MySQL-friendly timestamp value.
	 * 
	 * @param date
	 * @return
	 */
	protected String dateToMySQLTimestamp(Date date)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}
}
