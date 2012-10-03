package cz.cuni.mff.odcleanstore.webfrontend.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.wicket.DefaultMapperContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.IMapperContext;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.WebFrontendConfig;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.webfrontend.pages.HomePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;

/**
 * The OCDS - Web Frontend application class.
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 * 
 */
public class ODCSWebFrontendApplication extends AuthenticatedWebApplication 
{
	//private static Logger logger = Logger.getLogger(ODCSWebFrontendApplication.class);
	
	/** the application specific prefix of all URL addresses */
	private static final String WEB_URL_PREFIX = "odcs-web-frontend";
	
	private static final String APP_PROPERTIES_LOCATION = "config/application.properties";
	private static final String ODCS_PATH_PROPERTY = "odcs.config.path";
	
	/** a factory to lookup DAO objects */
	private DaoLookupFactory daoLookupFactory;
	
	/** application configuration object */
	private WebFrontendConfig configuration;
	
	/** utility to route URL addresses to components and vice versa */
	private URLRouter urlRouter;
	
	/**
	 * 
	 */
	public ODCSWebFrontendApplication() 
	{
		super();
		
		// Add request cycle listener that redirects to homepage with a proper message after session has expired 
		// getRequestCycleListeners().add(new ExceptionRequestCycleListener());
	}
	
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

		configuration = ConfigLoader.getConfig().getWebFrontendGroup();
		
		daoLookupFactory = new DaoLookupFactory(
			configuration.getCleanDBJDBCConnectionCredentials(),
			configuration.getDirtyDBJDBCConnectionCredentials()
		);
		
		getDebugSettings().setAjaxDebugModeEnabled(false);
		
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
	
	public WebFrontendConfig getConfiguration()
	{
		return configuration;
	}
}
