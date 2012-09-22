package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

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
	private static final String APP_PROPERTIES_LOCATION = "config/application.properties";
	private static final String ODCS_PATH_PROPERTY = "odcs.config.path";

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

		String odcsConfigPath = null;
		try {
			Properties props = loadProperties();
			odcsConfigPath = props.getProperty(ODCS_PATH_PROPERTY);
			ConfigLoader.loadConfig(odcsConfigPath);
		} catch (ConfigurationException e) {
			throw new RuntimeException("Loading global config failed: " + odcsConfigPath);
		} catch (IOException e) {
			throw new RuntimeException("Loading application properties failed: " + APP_PROPERTIES_LOCATION);
		}

		getDebugSettings().setAjaxDebugModeEnabled(false);
		
		ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_LOCATION);

		configuration = (Configuration) ctx.getBean("appConfig");
		
		daoLookupFactory = new DaoLookupFactory(
			configuration.getCleanConnectionCoords(),
			configuration.getDirtyConnectionCoords()
		);
		
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
	
	private Properties loadProperties() throws IOException
	{	
		Properties props = new Properties();
		InputStream inStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(APP_PROPERTIES_LOCATION);
        props.load(inStream);
        return props;
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
