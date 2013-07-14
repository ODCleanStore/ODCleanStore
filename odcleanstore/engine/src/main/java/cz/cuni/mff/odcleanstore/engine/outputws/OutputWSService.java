package cz.cuni.mff.odcleanstore.engine.outputws;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.ServiceState;

import org.restlet.Component;
import org.restlet.data.Protocol;

/**
 * @author Petr Jerman
 */
public final class OutputWSService extends Service {

    /**
     * Creates a new instance.
     * @param engine Engine instance which runs this service
     */
    public OutputWSService(Engine engine) {
        super(engine, "OutputWSService");
    }

    private Component component;

    @Override
    public void initialize() throws Exception {
        OutputWSConfig outputWSConfig = ConfigLoader.getConfig().getOutputWSGroup();
        System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
        component = new Component();
        component.getServers().add(Protocol.HTTP, outputWSConfig.getPort());
        component.getDefaultHost().attach(new Root(outputWSConfig, this));
        component.start();
    }

    @Override
    public void shutdown() throws Exception {
        if (component != null) {
            component.stop();
        }
    }

    @Override
    public String getServiceStateInfo() {
        if (isRunnningAndDbInstancesAvailable(false)) {
            return "OutputWS is running";
        }
        
        if (getServiceState() == ServiceState.RUNNING) {
            return "OutputWS is running, but clean db instance is not available";
        }
        
        return String.format("OutputWS is not running, has %s state", getServiceState().toString());
    }
}
