package cz.cuni.mff.odcleanstore.webfrontend.dao;

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
	 * 
	 * @param item
	 */
	public abstract void insert(T item);
	
	/**
	 * Find all entities in the database.
	 * 
	 * @return
	 */
	public abstract List<T> loadAll();
	
	/**
	 * Converts the given Date instance to a MySQL-friendly timestamp value.
	 * 
	 * @param date
	 * @return
	 */
	protected String dateToMySQLTimestamp(Date date)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		return
			calendar.get(Calendar.YEAR) + "-" +
			calendar.get(Calendar.MONTH) + "-" +
			calendar.get(Calendar.DAY_OF_MONTH) + " " +
			calendar.get(Calendar.HOUR_OF_DAY) + ":" +
			calendar.get(Calendar.MINUTE) + ":" +
			calendar.get(Calendar.SECOND);
	}
}
