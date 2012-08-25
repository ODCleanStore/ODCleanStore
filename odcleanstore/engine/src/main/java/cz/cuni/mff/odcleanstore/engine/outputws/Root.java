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
    private OutputWSService outputWSService;
    
    /**
     * Creates a new instance.
     * @param outputWSConfig configuration of the output webservice from the global configuration file
     * @param outputWSService OutputWSService instance
     */
    public Root(OutputWSConfig outputWSConfig, OutputWSService outputWSService) {
        this.outputWSConfig = outputWSConfig;
        this.outputWSService = outputWSService;
        setStatusService(new StatusService());
    }

    /**
     * Creates a root Restlet that will receive all incoming calls.
     * @return router
     */
    @Override
    public synchronized Restlet createInboundRoot() {

        Router router = new Router(getContext());
        router.attach("/" + outputWSConfig.getKeywordPath(), KeywordQueryExecutorResource.class);
        router.attach("/" + outputWSConfig.getUriPath(), UriQueryExecutorResource.class);
        router.attach("/" + outputWSConfig.getNamedGraphPath(), NamedGraphQueryExecutorResource.class);
        return router;
    }

    /**
     * Indicates whether hte service is running and can serve requests.
     * @return true iff the srevice is running
     */
    public boolean canServeRequest() {
        return outputWSService.isRunnningAndDbInstancesAvailable(false);
    }
}