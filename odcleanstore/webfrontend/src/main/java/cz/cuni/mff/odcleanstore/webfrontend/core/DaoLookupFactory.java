package cz.cuni.mff.odcleanstore.webfrontend.core;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RoleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.UserDao;

import org.apache.wicket.proxy.LazyInitProxyFactory;
import org.apache.wicket.spring.SpringBeanLocator;

/**
 * A factory to lookup DAO Spring beans.
 *  
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class DaoLookupFactory 
{
	private UserDao userDao;
	private RoleDao roleDao;
	
	/**
	 * 
	 * @return
	 */
	public UserDao getUserDao()
	{
		if (userDao == null)
			userDao = createProxy("userDao", UserDao.class);
		
		return userDao;
	}
	
	/**
	 * 
	 * @return
	*/
	public RoleDao getRoleDao()
	{
		if (roleDao == null)
			roleDao = createProxy("roleDao", RoleDao.class);
		
		return roleDao;
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
