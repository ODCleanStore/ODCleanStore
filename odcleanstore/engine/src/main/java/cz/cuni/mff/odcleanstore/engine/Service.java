/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import cz.cuni.mff.odcleanstore.engine.common.Module;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 *  @author Petr Jerman
 */
public abstract class Service extends Module {
	
	private final Engine engine;
	protected final String engineUuid;

	protected Service(Engine engine) {
		if (engine == null) {
			throw new IllegalArgumentException();
		}
		this.engine = engine;
		this.engineUuid = engine.getEngineUuid();
	}

	@Override
	protected final void setModuleState(ModuleState _moduleState) {
		super.setModuleState(_moduleState);
		engine.onServiceStateChanged(this);
	}
	
	public abstract void shutdown(); 
}