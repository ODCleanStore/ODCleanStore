package cz.cuni.mff.odcleanstore.webfrontend;

import org.apache.wicket.protocol.http.WebApplication;

/**
 * Web Frontend Application object.
 * 
 */
public class WicketApplication extends WebApplication {
	@Override
	public Class<HomePage> getHomePage() {
		return HomePage.class;
	}

	@Override
	public void init() {
		super.init();

		// configuration is to be added here
	}
}
