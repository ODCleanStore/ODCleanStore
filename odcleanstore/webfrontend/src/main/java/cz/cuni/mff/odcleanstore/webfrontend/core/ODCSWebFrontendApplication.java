package cz.cuni.mff.odcleanstore.webfrontend.core;

import org.apache.wicket.DefaultMapperContext;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.IMapperContext;
import org.apache.wicket.spring.ISpringContextLocator;
import org.apache.wicket.util.lang.PackageName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cz.cuni.mff.odcleanstore.webfrontend.configuration.Configuration;
import cz.cuni.mff.odcleanstore.webfrontend.pages.HomePage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.LogInPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.outputws.AggregationSettingsPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.PipelinesManagementPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.pipelines.TransformersManagementPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.oi.OIRulesManagementPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.transformers.qa.QARulesManagementPage;
import cz.cuni.mff.odcleanstore.webfrontend.pages.useraccounts.UserAccountsPage;

/**
 * Web Frontend Application object.
 * 
 */
public class ODCSWebFrontendApplication extends AuthenticatedWebApplication 
{
	private static final String WEB_URL_PREFIX = "odcs-web-frontend";
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
		
		mountPage(WEB_URL_PREFIX + "/login", LogInPage.class);
		mountPage(WEB_URL_PREFIX + "/user-accounts", UserAccountsPage.class);
		mountPage(WEB_URL_PREFIX + "/backend/pipelines", PipelinesManagementPage.class);
		mountPage(WEB_URL_PREFIX + "/backend/transformers", TransformersManagementPage.class);
		mountPage(WEB_URL_PREFIX + "/backend/rules/oi", OIRulesManagementPage.class);
		mountPage(WEB_URL_PREFIX + "/backend/rules/qa", QARulesManagementPage.class);
		mountPage(WEB_URL_PREFIX + "/output-ws/aggregations", AggregationSettingsPage.class);
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
