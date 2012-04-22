/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.Component;
import org.restlet.data.Protocol;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 * @author jermanp
 * 
 */
public class UserService extends Service implements Runnable {

	public UserService(Engine engine) {
		super(engine);
	}

	private Component _component;

	@Override
	public final void run() {
		try {
			if (getModuleState() != ModuleState.NEW) {
				return;
			}

			setModuleState(ModuleState.INITIALIZING);
			_component = new Component();
			_component.getServers().add(Protocol.HTTP, Engine.USER_SERVICE_PORT);
			_component.getDefaultHost().attach(new Root());
			_component.start();
			setModuleState(ModuleState.RUNNING);
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
		}
	}
}
