package cz.cuni.mff.odcleanstore.engine.outputws;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;

/**
 *  @author Petr Jerman
 */
public class Root extends Application {
    /** Configuration of the output webservice from the global configuration file. */
    private OutputWSConfig outputWSConfig;
    
    /**
     * Creates a new instance.
     * @param outputWSConfig configuration of the output webservice from the global configuration file
     */
	public Root(OutputWSConfig outputWSConfig) {
		this.outputWSConfig = outputWSConfig;
		setStatusService(new StatusService());
	}

	/**
	 * Creates a root Restlet that will receive all incoming calls.
	 */
	@Override
	public synchronized Restlet createInboundRoot() {

		Router router = new Router(getContext());
		router.attach("/" + outputWSConfig.getKeywordPath(), KeywordQueryExecutorResource.class);
		router.attach("/" + outputWSConfig.getUriPath(), UriQueryExecutorResource.class);
		router.attach("/" + outputWSConfig.getNamedGraphPath(), NamedGraphQueryExecutorResource.class);
		return router;
	}
}