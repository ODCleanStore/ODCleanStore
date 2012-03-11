package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.markup.html.basic.Label;

public class HomePage extends FrontendPage {
	private static final long serialVersionUID = 1L;

	public HomePage() {
		add(new Label("pageTitle", "Welcome to ODCleanStore Web Frontend"));
	}
}
