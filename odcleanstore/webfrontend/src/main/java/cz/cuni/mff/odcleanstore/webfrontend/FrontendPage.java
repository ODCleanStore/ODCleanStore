package cz.cuni.mff.odcleanstore.webfrontend;

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

	/**
	 * 
	 * @param pageCrumbs the page-crumbs in the form of a simple string
	 * to be rendered using a label component
	 * @param pageTitle
	 */
	public FrontendPage(String pageCrumbs, String pageTitle)
	{
		add(new Label("pageCrumbs", pageCrumbs));
		add(new Label("pageTitle", pageTitle));
		
		add(new UserPanel("userPanel", LogOutPage.class));
		
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
}
