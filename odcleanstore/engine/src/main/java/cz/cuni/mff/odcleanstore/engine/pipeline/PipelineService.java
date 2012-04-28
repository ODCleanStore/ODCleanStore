package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.transformer.TransformerException;

public final class PipelineService extends Service implements Runnable {

	public PipelineService(Engine engine) {
		super(engine);
	}

	@Override
	public void run() {
		if (getModuleState() != ModuleState.NEW) {
			return;
		}

		setModuleState(ModuleState.INITIALIZING);
		WorkingInputGraphStatus wis = new WorkingInputGraphStatus(this);
		try {
			Collection<String> graphsForRecoveryUuids = wis.getWorkingTransformedGraphUuids();
			if (!graphsForRecoveryUuids.isEmpty()) {
				setModuleState(ModuleState.RECOVERY);
				recovery(graphsForRecoveryUuids);
			}
			setModuleState(ModuleState.RUNNING);
			runPipeline();
			setModuleState(ModuleState.STOPPED);
		} catch (TransformerException e) {
			setModuleState(ModuleState.CRASHED);
		}
	}

	private void recovery(Collection<String> graphsForRecoveryUuids) {

	}

	private void runPipeline() {

	}
}