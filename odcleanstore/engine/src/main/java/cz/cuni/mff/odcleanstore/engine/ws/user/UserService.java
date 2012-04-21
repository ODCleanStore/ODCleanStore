/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.user;

import org.restlet.Component;
import org.restlet.data.Protocol;

import cz.cuni.mff.odcleanstore.engine.Module;
import cz.cuni.mff.odcleanstore.engine.ModuleState;

/**
 * @author jermanp
 * 
 */
public class UserService extends Module {

	public UserService(Module parent) {
		super(parent);
	}

	private Component _component;

	@Override
	public void run() {
		try {
			if (get_moduleState() != ModuleState.NEW) {
				return;
			}
			set_moduleState(ModuleState.INITIALIZING);
			_component = new Component();
			_component.getServers().add(Protocol.HTTP, 8087);
			_component.getDefaultHost().attach(new Root());
			_component.start();
			set_moduleState(ModuleState.RUNNING);
		} catch (Exception e) {
			set_moduleState(ModuleState.CRASHED);
		}
	}
}
