package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.Serializable;
import java.util.HashMap;

import javax.sql.DataSource;

import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.SafetyDaoDecorator;
import cz.cuni.mff.odcleanstore.webfrontend.dao.cr.GlobalAggregationSettingsDao;

import org.apache.wicket.proxy.LazyInitProxyFactory;
import org.apache.wicket.spring.SpringBeanLocator;

/**
 * A factory to lookup DAO Spring beans.
 *  
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DaoLookupFactory implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private DataSource dataSource;
	private HashMap<Class<? extends Dao>, Dao> daos;
	
	private GlobalAggregationSettingsDao globalAggregationSettingsDao;
	
	/**
	 * 
	 */
	public DaoLookupFactory()
	{
		this.dataSource = createProxy("dataSource", DataSource.class);
		this.daos = new HashMap<Class<? extends Dao>, Dao>();
	}
	
	/**
	 * Creates (lazily) and returns the requested DAO object decorated by
	 * a SafetyDaoDecorator instance. 
	 * 
	 * Throws an AssertionError if the requested DAO class cannot be 
	 * instantiated.
	 * 
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	public Dao getDao(Class<? extends Dao> daoClass) throws AssertionError
	{
		if (daos.containsKey(daoClass))
			return daos.get(daoClass);
		
		Dao daoInstance = createDaoInstance(daoClass);
		Dao safeDaoInstance = new SafetyDaoDecorator(daoInstance);
		
		daos.put(daoClass, safeDaoInstance);
		
		return safeDaoInstance;
	}
	
	/**
	 * Creates and returns a bew raw (e.g. undecorated) instance of the
	 * requested DAO class.
	 * 
	 * Throws an AssertionError if the requested DAO class cannot be 
	 * instantiated.
	 * 
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	public Dao getUnsafeDao(Class<? extends Dao> daoClass) throws AssertionError
	{
		return createDaoInstance(daoClass);
	}
	
	/**
	 * Creates and returns a DAO instance related to the given class.
	 *  
	 * @param daoClass
	 * @return
	 * @throws AssertionError
	 */
	private Dao createDaoInstance(Class<? extends Dao> daoClass) throws AssertionError
	{
		Dao daoInstance;
		
		try {
			daoInstance = daoClass.newInstance();
		} 
		catch (Exception ex) 
		{
			throw new AssertionError(
				"Could not load DAO class: " + daoClass
			);
		}

		daoInstance.setDataSource(dataSource);
		
		return daoInstance;
	}
	
	/**
	 * 
	 * @return
	 */
	public GlobalAggregationSettingsDao getGlobalAggregationSettingsDao()
	{
		if (globalAggregationSettingsDao == null)
		{
			globalAggregationSettingsDao = createProxy(
				"globalAggregationSettingsDao", 
				GlobalAggregationSettingsDao.class
			);
		}
		
		return globalAggregationSettingsDao;
	}

	/**
	 * Helper method to create a proxy of the bean. This is needed not to
	 * serialize the whole Spring framework when storing a page to cache.
	 * 
	 * @param beanName
	 * @param beanClass
	 * @return
	 */
	private <T> T createProxy(String beanName, Class<T> beanClass)
	{
		return (T)LazyInitProxyFactory.createProxy(
			beanClass, 
			new SpringBeanLocator(beanName, beanClass, WicketApplication.CTX_LOCATOR)
		);
	}
}
