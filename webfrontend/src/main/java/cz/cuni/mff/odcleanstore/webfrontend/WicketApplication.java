package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.ISpringContextLocator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Web Frontend Application object.
 * 
 */
public class WicketApplication extends WebApplication 
{
	static ISpringContextLocator CTX_LOCATOR = new ISpringContextLocator() 
	{	
		public ApplicationContext getSpringContext() 
		{
			return ((WicketApplication) WicketApplication.get()).ctx;
		}
	};
	
	private ApplicationContext ctx;
	private DaoLookupFactory daoLookupFactory;
	
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
		daoLookupFactory = new DaoLookupFactory();
	}

	public DaoLookupFactory getDaoLookupFactory()
	{
		return daoLookupFactory;
	}
}