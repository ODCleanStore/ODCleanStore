package cz.cuni.mff.odcleanstore.engine.common;

public class Module {
	
	private ModuleState _moduleState = ModuleState.NEW;

	protected Module() {
	}

	public final ModuleState getModuleState() {
		return _moduleState;
	}

	protected void setModuleState(ModuleState _moduleState) {
		this._moduleState = _moduleState;
	}
}
