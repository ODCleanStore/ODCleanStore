package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * An abstract base class for all WebFrontend page components, except for
 * some meta-page-components (like the LogOutPage component).
 * 
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public abstract class FrontendPage extends WebPage 
{
	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(FrontendPage.class);
	
	protected DaoLookupFactory daoLookupFactory;
	
	/**
	 * 
	 * @param pageCrumbs the page-crumbs in the form of a simple string
	 * to be rendered using a label component
	 * @param pageTitle
	 */
	public FrontendPage(String pageCrumbs, String pageTitle)
	{
		// obtain the DAO-lookup-factory
		//
		daoLookupFactory = getApp().getDaoLookupFactory();
		
		// add common page components
		//
		add(new Label("pageCrumbs", pageCrumbs));
		add(new Label("pageTitle", pageTitle));
		
		// add(new UserPanel("userPanel", LogOutPage.class));
		
		add(new FeedbackPanel("feedback"));
	}
	
	/**
	 * 
	 * @return
	 */
	protected WicketApplication getApp() 
	{
		return (WicketApplication) this.getApplication();
	}
	
	/**
	 * 
	 */
	@Override
	protected void onDetach()
	{
		
		
		// according to the Wicket javadoc documentation, the super implementation
		// should be called at the last line
		// (see http://wicket.apache.org/apidocs/1.4/org/apache/wicket/Page.html#onDetach())
		super.onDetach();
	}
}
