package cz.cuni.mff.odcleanstore.webfrontend.core;

import org.apache.wicket.DefaultMapperContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.IMapperContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.webfrontend.configuration.Configuration;
import cz.cuni.mff.odcleanstore.webfrontend.pages.HomePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;

/**
 * Web Frontend Application object.
 * 
 */
public class ODCSWebFrontendApplication extends AuthenticatedWebApplication 
{
	private static final String WEB_URL_PREFIX = "odcs-web-frontend";
	private static final String SPRING_CONFIG_LOCATION = "./config/spring.xml";
	private static final String GLOBAL_CONFIG_LOCATION = "../../data/odcs_configuration/odcs.ini";

	/** Spring context */
	private ApplicationContext ctx;
	
	/** A factory to lookup Spring beans */
	private DaoLookupFactory daoLookupFactory;
	
	/** Application configuration */
	private Configuration configuration;
	
	private URLRouter urlRouter;
	
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
		
		try {
			ConfigLoader.loadConfig(GLOBAL_CONFIG_LOCATION);
		} catch (ConfigurationException e) {
			throw new RuntimeException("Loading global config failed: " + GLOBAL_CONFIG_LOCATION);
		}
		
		ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);

		configuration = (Configuration) ctx.getBean("appConfig");
		daoLookupFactory = new DaoLookupFactory(configuration.getConnectionCoords());
		
		urlRouter = new URLRouter(WEB_URL_PREFIX);
		urlRouter.setupRouting(this);
	}
	
	@Override
	public IMapperContext newMapperContext()
	{
		return new DefaultMapperContext()
		{
			@Override 
			public String getNamespace() 
			{
				return WEB_URL_PREFIX; 
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
