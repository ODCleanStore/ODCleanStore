package cz.cuni.mff.odcleanstore.engine.pipeline;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

public final class PipelineService extends Service implements Runnable {

	public PipelineService(Engine engine) {
		super(engine);
	}

	@Override
	public void run() {
		// _workingInputGraphState = new WorkingInputGraphState();
		setModuleState(ModuleState.STOPPED);
	}

	static WorkingInputGraphState getWorkingInputGraphState(TransformedGraphImpl transformedGraphImpl) {
		PipelineService service = Service.getPipelineService();
		return service != null && service._currentTransformedGraphImpl == transformedGraphImpl ? service._workingInputGraphState
				: null;
	}

	private WorkingInputGraphState _workingInputGraphState;
	private TransformedGraphImpl _currentTransformedGraphImpl;
}
