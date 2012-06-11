package cz.cuni.mff.odcleanstore.engine.outputws;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 *  @author Petr Jerman
 */
public final class OutputWSService extends Service implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(OutputWSService.class);

	public OutputWSService(Engine engine) {
		super(engine);
	}

	private Component _component;

	@Override
	public void run() {
		try {
			synchronized (this) {
				if (getModuleState() != ModuleState.NEW) {
					return;
				}
				setModuleState(ModuleState.INITIALIZING);
				LOG.info("OutputWSService initializing");
			}
			System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
			_component = new Component();
			_component.getServers().add(Protocol.HTTP, Engine.OUTPUTWS_PORT);
			_component.getDefaultHost().attach(new Root());
			_component.start();
			setModuleState(ModuleState.RUNNING);
			LOG.info("OutputWSService running");
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
			String message = String.format("OutputWSService crashed - %s", e.getMessage());
			LOG.fatal(message);
		}
	}
}