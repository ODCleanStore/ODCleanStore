package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.engine.Module;
import cz.cuni.mff.odcleanstore.engine.ModuleState;

public class PipelineService extends Module {

	public PipelineService(Module parent) {
		super(parent);
	}

	@Override
	public void run() {
		set_moduleState(ModuleState.STOPPED);
	}
}
