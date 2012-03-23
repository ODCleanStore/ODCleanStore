package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

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
	 * Find all entities in the database.
	 * 
	 * @return
	 */
	public abstract List<T> loadAll();
}
