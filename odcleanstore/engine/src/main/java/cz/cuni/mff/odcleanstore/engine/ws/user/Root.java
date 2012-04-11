/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class Root extends Application {

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {

		Router router = new Router(getContext());
		router.attach("/qe/keyword", KeywordQueryExecutorResource.class);
		return router;
	}
}