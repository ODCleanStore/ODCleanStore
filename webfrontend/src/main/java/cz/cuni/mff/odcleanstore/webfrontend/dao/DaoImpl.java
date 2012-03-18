package cz.cuni.mff.odcleanstore.webfrontend.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * Generic Hibernate-based DAO implementation. 
 * 
 * Allows iterating through, creating, modifying and deleting rows
 * in a database table. The entity type (e.g. table) is given
 * using the generic parameter and the Class in the constructor. 
 * 
 * @author Dusan Rychnovsky (dusan.rychnovsky@gmail.com)
 *
 */
public class DaoImpl<T> extends HibernateDaoSupport implements Dao<T> 
{
	private Class<T> domainClass;

	/**
	 * 
	 * @param domainClass
	 */
	public DaoImpl(Class<T> domainClass)
	{
		this.domainClass = domainClass;
	}

	/**
	 * Save the given entity to the database.
	 * 
	 * @param item
	 */
	public void save(T item) 
	{
		getHibernateTemplate().save(item);
	}

	/**
	 * Update the given entity in the database.
	 * 
	 * @param item
	 */
	public void update(T item) 
	{
		getHibernateTemplate().update(item);	
	}

	/**
	 * Delete the given entity from the database.
	 * 
	 * @param item
	 */
	public void delete(T item) 
	{
		getHibernateTemplate().delete(item);	
	}

	/**
	 * Find all entities in the database.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll() 
	{
		return getHibernateTemplate().loadAll(domainClass);
	}
	
}
