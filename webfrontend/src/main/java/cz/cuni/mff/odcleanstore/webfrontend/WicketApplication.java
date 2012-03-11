package cz.cuni.mff.odcleanstore.webfrontend;

import java.util.Date;

import org.apache.wicket.protocol.http.WebApplication;

import cz.cuni.mff.odcleanstore.webfrontend.dao.User;

/**
 * Web Frontend Application object.
 * 
 */
public class WicketApplication extends WebApplication {

	public static final User[] users = {
			new User("karelv", "vasicek@seznam.cz", new Date()),
			new User("hp", "hp@gmail.com", new Date()),
			new User("knovak", "knovak@gmail.com", new Date()) };

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
