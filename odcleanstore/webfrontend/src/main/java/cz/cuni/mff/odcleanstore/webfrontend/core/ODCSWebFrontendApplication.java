package cz.cuni.mff.odcleanstore.webfrontend.core;

import org.apache.wicket.DefaultMapperContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.IMapperContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.mff.odcleanstore.webfrontend.configuration.Configuration;
import cz.cuni.mff.odcleanstore.webfrontend.pages.HomePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.CRPropertiesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.PipelinesListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.TransformersListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIGroupsListPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QAGroupDetailPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.UserAccountsPage;

/**
 * Web Frontend Application object.
 * 
 */
public class ODCSWebFrontendApplication extends AuthenticatedWebApplication 
{
	private static final String WEB_URL_PREFIX = "odcs-web-frontend";
	private static final String SPRING_CONFIG_LOCATION = "./config/spring.xml";

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
		
		getDebugSettings().setAjaxDebugModeEnabled(false);
		
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
