package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

public abstract class FrontendPage extends WebPage {

	private static final long serialVersionUID = 1L;

	public FrontendPage(String pageCrumbs, String pageTitle) {
		add(new Label("pageCrumbs", pageCrumbs));
		add(new Label("pageTitle", pageTitle));
	}

	protected WicketApplication getApp() {
		return (WicketApplication) this.getApplication();
	}
}