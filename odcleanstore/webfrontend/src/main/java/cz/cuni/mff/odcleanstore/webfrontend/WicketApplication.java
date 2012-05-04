package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.ISpringContextLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.mff.odcleanstore.webfrontend.configuration.Configuration;

/**
 * Web Frontend Application object.
 * 
 */
public class WicketApplication extends WebApplication 
{
	private static final String SPRING_CONFIG_LOCATION = "./config/spring.xml";
	
	static ISpringContextLocator CTX_LOCATOR = new ISpringContextLocator() 
	{	
		public ApplicationContext getSpringContext() 
		{
			return ((WicketApplication) WicketApplication.get()).ctx;
		}
	};

	/** Spring context */
	private ApplicationContext ctx;
	
	/** A factory to lookup Spring beans */
	private DaoLookupFactory daoLookupFactory;
	
	/** Application configuration */
	private Configuration configuration;
	
	
	@Override
	public Class<HomePage> getHomePage() 
	{
		return HomePage.class;
	}

	@Override
	public void init() 
	{
		super.init();
		
		ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
		daoLookupFactory = new DaoLookupFactory();
		configuration = (Configuration) ctx.getBean("appConfig");
	}
	
	/**
	 * 
	 * @return
	 */
	public DaoLookupFactory getDaoLookupFactory()
	{
		return daoLookupFactory;
	}
	
	public Configuration getConfiguration()
	{
		return configuration;
	}
}
