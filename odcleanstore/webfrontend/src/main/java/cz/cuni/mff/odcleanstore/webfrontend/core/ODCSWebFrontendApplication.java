package cz.cuni.mff.odcleanstore.webfrontend.core;

import org.apache.wicket.DefaultMapperContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.IMapperContext;
import org.apache.wicket.spring.ISpringContextLocator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.mff.odcleanstore.webfrontend.configuration.Configuration;
import cz.cuni.mff.odcleanstore.webfrontend.pages.HomePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;

/**
 * Web Frontend Application object.
 * 
 */
public class ODCSWebFrontendApplication extends AuthenticatedWebApplication 
{
	private static final String SPRING_CONFIG_LOCATION = "./config/spring.xml";
	
	static ISpringContextLocator CTX_LOCATOR = new ISpringContextLocator() 
	{	
		public ApplicationContext getSpringContext() 
		{
			return ((ODCSWebFrontendApplication) ODCSWebFrontendApplication.get()).ctx;
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
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() 
	{
		return ODCSWebFrontendSession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() 
	{
		return LogInPage.class;
	}
	
	@Override
	public void init() 
	{
		super.init();
		
		ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);
		daoLookupFactory = new DaoLookupFactory();
		configuration = (Configuration) ctx.getBean("appConfig");
	}
	
	@Override
	public IMapperContext newMapperContext()
	{
		return new DefaultMapperContext()
		{
			@Override 
			public String getNamespace() 
			{
				return "odcs-web-frontend"; 
			}
		};
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
