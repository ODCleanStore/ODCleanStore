package cz.cuni.mff.odcleanstore.webfrontend;


import cz.cuni.mff.odcleanstore.webfrontend.bo.User;
import cz.cuni.mff.odcleanstore.webfrontend.dao.Dao;

import org.apache.wicket.protocol.http.WebApplication;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Web Frontend Application object.
 * 
 */
public class WicketApplication extends WebApplication 
{
	private ApplicationContext ctx;
	
	@Override
	public Class<HomePage> getHomePage() 
	{
		return HomePage.class;
	}

	@Override
	public void init() 
	{
		super.init();
		
		ctx = new ClassPathXmlApplicationContext("./config/bean_locations.xml");
	}
	
	public Dao<User> getUserDao()
	{
		return (Dao<User>) ctx.getBean("userDao");
	}
}
