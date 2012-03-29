package cz.cuni.mff.odcleanstore.webfrontend;

import cz.cuni.mff.odcleanstore.webfrontend.bo.Role;
import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.RoleDao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.UserDao;

import org.apache.wicket.proxy.LazyInitProxyFactory;
import org.apache.wicket.spring.SpringBeanLocator;

public class DaoLookupFactory 
{
	private Dao<User> userDao;
	private Dao<Role> roleDao;
	
	private <T> T createProxy(String beanName, Class<T> beanClass)
	{
		return (T)LazyInitProxyFactory.createProxy(
			beanClass, 
			new SpringBeanLocator(beanName, beanClass, WicketApplication.CTX_LOCATOR)
		);
	}
	
	public Dao<User> getUserDao()
	{
		if (userDao == null)
			userDao = createProxy("userDao", UserDao.class);
		
		return userDao;
	}
	
	public Dao<Role> getRoleDao()
	{
		if (roleDao == null)
			roleDao = createProxy("roleDao", RoleDao.class);
		
		return roleDao;
	}
}
