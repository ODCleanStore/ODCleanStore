/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

/**
 * @author jermanp
 * 
 */
public abstract class Module implements Runnable {
	
	private ModuleState _moduleState = ModuleState.NEW;
	private Module _parent;
	
	public Module(Module parent) {
		_parent = parent;
	}
		
	public final ModuleState get_moduleState() {
		return _moduleState;
	}
	
	protected final void set_moduleState(ModuleState _moduleState) {
		this._moduleState = _moduleState;
		if (_parent != null) {
			_parent.onChildStateChanged(this);
		}
	}

	protected void onChildStateChanged(Module child) {
	}
}
