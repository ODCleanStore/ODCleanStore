package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.proxy.LazyInitProxyFactory;
import org.apache.wicket.spring.SpringBeanLocator;

import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;
import cz.cuni.mff.odcleanstore.webfrontend.dao.UserDaoImpl;

public class DaoLookupFactory 
{
	private Dao<User> userDao;
	
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
			userDao = createProxy("userDao", UserDaoImpl.class);
		
		return userDao;
	}
}
