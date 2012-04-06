package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.ISpringContextLocator;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Web Frontend Application object.
 * 
 */
public class WicketApplication extends AuthenticatedWebApplication 
{
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
	
	@Override
	public Class<HomePage> getHomePage() 
	{
		return HomePage.class;
	}

	@Override
	public void init() 
	{
		super.init();
		
		ctx = new ClassPathXmlApplicationContext("./config/spring.xml");
		daoLookupFactory = new DaoLookupFactory();
	}

	/**
	 * 
	 * @return
	 */
	public DaoLookupFactory getDaoLookupFactory()
	{
		return daoLookupFactory;
	}

	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() 
	{
		return WicketSession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() 
	{
		return LogInPage.class;
	}
}
