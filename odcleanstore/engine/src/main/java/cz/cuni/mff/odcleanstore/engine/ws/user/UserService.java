/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 * @author jermanp
 * 
 */
public final class UserService extends Service implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(UserService.class);

	public UserService(Engine engine) {
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
				LOG.info("UserService initializing");
			}
			System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade");
			_component = new Component();
			_component.getServers().add(Protocol.HTTP, Engine.USER_SERVICE_PORT);
			_component.getDefaultHost().attach(new Root());
			_component.start();
			setModuleState(ModuleState.RUNNING);
			LOG.info("UserService running");
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
			String message = String.format("UserService crashed - %s", e.getMessage());
			LOG.fatal(message);
		}
	}
}
