package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import cz.cuni.mff.odcleanstore.engine.Engine;

/**
 *  @author Petr Jerman
 */
public class Root extends Application {

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {

		Router router = new Router(getContext());
		router.attach("/" + Engine.USER_SERVICE_KEYWORD_PATH, KeywordQueryExecutorResource.class);
		router.attach("/" + Engine.USER_SERVICE_URI_PATH, UriQueryExecutorResource.class);
		return router;
	}
}