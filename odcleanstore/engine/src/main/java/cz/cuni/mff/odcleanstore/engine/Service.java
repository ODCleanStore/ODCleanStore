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
	
	private final Engine _engine;

	protected Service(Engine engine) {
		if (engine == null) {
			throw new IllegalArgumentException();
		}
		this._engine = engine;
	}

	@Override
	protected final void setModuleState(ModuleState _moduleState) {
		super.setModuleState(_moduleState);
		_engine.onServiceStateChanged(this);
	}
	
	public abstract void shutdown(); 
}