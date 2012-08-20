package cz.cuni.mff.odcleanstore.engine.outputws;

import org.restlet.Component;
import org.restlet.data.Protocol;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.OutputWSConfig;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;

/**
 *  @author Petr Jerman
 */
public final class OutputWSService extends Service {
	
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
}
