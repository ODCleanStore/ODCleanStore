package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

/**
 * Generic DAO interface.
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
public interface Dao<T> 
{
	/**
	 * Save the given entity to the database.
	 * 
	 * @param item
	 */
	public void save(T item);

	/**
	 * Update the given entity in the database.
	 * 
	 * @param item
	 */
	public void update(T item);
	
	/**
	 * Delete the given entity from the database.
	 * 
	 * @param item
	 */
	public void delete(T item);
	
	/**
	 * Find all entities in the database.
	 * 
	 * @return
	 */
	public List<T> findAll();
}
