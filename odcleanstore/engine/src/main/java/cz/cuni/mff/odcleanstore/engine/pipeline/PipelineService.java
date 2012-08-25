package cz.cuni.mff.odcleanstore.engine.pipeline;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.ServiceState;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.model.GraphStates;
import cz.cuni.mff.odcleanstore.engine.db.model.PipelineErrorTypes;

/**
 *  @author Petr Jerman
 */
public final class PipelineService extends Service implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(PipelineService.class);
	
	private Object waitForGraphLock;
	private Object waitPenaltyLock;
	private PipelineGraphTransformerExecutor activeTransformerExecutor;
	
	public PipelineService(Engine engine) {
		super(engine, "PipelineService");
		this.waitForGraphLock = new Object();
		this.waitPenaltyLock = new Object();
		this.activeTransformerExecutor = null;
	}

	@Override
	public void shutdown() throws Exception {
		synchronized (waitForGraphLock) {
			waitForGraphLock.notify();
		}
		synchronized (waitPenaltyLock) {
			waitPenaltyLock.notify();
		}
		if (activeTransformerExecutor != null) {
			activeTransformerExecutor.shutdown();
		}
	}

	public void notifyAboutGraphForPipeline() {
		synchronized (waitForGraphLock) {
			waitForGraphLock.notify();
		}
	}
	
	private PipelineGraphStatus waitForGraphForPipeline() throws PipelineGraphStatusException, InterruptedException {
		while(getServiceState() == ServiceState.RUNNING) {
			PipelineGraphStatus status  = PipelineGraphStatus.getNextGraphForPipeline(engine.getEngineUuid());
			if (status != null) {
				return status;
			}
			synchronized (waitForGraphLock) {
				if (getServiceState() == ServiceState.RUNNING) {
					// TODO to global config					
					waitForGraphLock.wait(8000);
				}
			}
		}
		return null;	
	}
	
	@Override
	public void execute() {
		long _waitPenalty = 0;
		while (getServiceState() == ServiceState.RUNNING) {
			try {
				if (_waitPenalty > 1) {
					synchronized (waitPenaltyLock) {
						if (getServiceState() == ServiceState.RUNNING) {
							// TODO to global config
							waitPenaltyLock.wait(80000);
						}
					}
				}
				PipelineGraphStatus status = null;				
				while ((status = waitForGraphForPipeline()) != null) {
					executePipeline(status);
					_waitPenalty = 0;
				}
			} catch (Exception e) {
					_waitPenalty++;
					LOG.error(FormatHelper.formatExceptionForLog(e, "Pipeline crashed"));
			}
		}
	}
	
	private void executePipeline(PipelineGraphStatus status) 
			throws PipelineGraphManipulatorException, PipelineGraphTransformerExecutorException, PipelineGraphStatusException {
		
		PipelineGraphManipulator manipulator = new PipelineGraphManipulator(status); 
		while (getServiceState() == ServiceState.RUNNING) {
			switch (status.getState()) {
				case DELETING:
					executeStateDeleting(manipulator, status);
					break;
				case PROCESSING:
					executeStateProcessing(manipulator, status);
					break;
				case PROCESSED:
					executeStateProcessed(manipulator, status);
					break;
				case PROPAGATED:
					executeStatePropagated(manipulator, status);
					break;
				case DIRTY:
					executeStateDirty(manipulator, status);
					break;
				default:
					return;
			}
		}
	}
	
	private void executeStateDeleting(PipelineGraphManipulator manipulator, PipelineGraphStatus status)
			throws PipelineGraphManipulatorException, PipelineGraphStatusException {

		LOG.info(format("deleting started", status));
		manipulator.deleteGraphsInCleanDB();
		manipulator.deleteGraphsInDirtyDB();
		manipulator.deleteInputFile();
		status.setNoDirtyState(GraphStates.DELETED);
		LOG.info(format("deleting successfully finished", status));
	}
	
	private void executeStateProcessing(PipelineGraphManipulator manipulator, PipelineGraphStatus status)
			throws PipelineGraphManipulatorException, PipelineGraphStatusException, PipelineGraphTransformerExecutorException {
		
		LOG.info(format("processing in dirty db started", status));
		manipulator.deleteGraphsInDirtyDB();
		if (!status.isInCleanDb()) {
			status.deleteAttachedGraphs();
		}
		executeStateProcessingLoadData(manipulator, status);
		executeStateProcessingTransformers(status);
		if (status.isMarkedForDeleting()) {
			status.setNoDirtyState(GraphStates.DELETING);
			LOG.info(format("processing in dirty db successfully finished, graph is marked for deleting", status));
		}
		else {
			status.setNoDirtyState(GraphStates.PROCESSED);
			LOG.info(format("processing in dirty db successfully finished", status));
		}
	}
	
	private void executeStateProcessingLoadData(PipelineGraphManipulator manipulator, PipelineGraphStatus status)
			throws PipelineGraphManipulatorException, PipelineGraphStatusException {

		try {
			manipulator.loadGraphsIntoDirtyDB();
		} catch (PipelineGraphManipulatorException e) {
			if(isRunnningAndDbInstancesAvailable(true)) {
				String message = FormatHelper.formatExceptionForLog(e, format("data loading failure", status));
				status.setDirtyState(PipelineErrorTypes.DATA_LOADING_FAILURE, message);
			}
			throw e; 		
		}
	}
	
	private void executeStateProcessingTransformers(PipelineGraphStatus status)
			throws PipelineGraphTransformerExecutorException, PipelineGraphStatusException  {

		try {
			this.activeTransformerExecutor = new PipelineGraphTransformerExecutor(status);
			activeTransformerExecutor.execute();
		} catch (PipelineGraphTransformerExecutorException e) {
			if(isRunnningAndDbInstancesAvailable(true)) {
				String message = FormatHelper.formatExceptionForLog(e, format("transformer processing failure", status));
				status.setDirtyState(PipelineErrorTypes.TRANSFORMER_FAILURE, message);
			}
			throw e;
		}
		finally {
			this.activeTransformerExecutor = null;
		}
	}
	
	private void executeStateProcessed(PipelineGraphManipulator manipulator, PipelineGraphStatus status) 
			throws PipelineGraphManipulatorException, PipelineGraphStatusException {
		
		LOG.info(format("replacing from dirty to clean db started", status));
		manipulator.replaceGraphsInCleanDBFromDirtyDB();
		status.setNoDirtyState(GraphStates.PROPAGATED);
		LOG.info(format("replacing from dirty to clean db successfully finished", status));
	}

	private void executeStatePropagated(PipelineGraphManipulator manipulator, PipelineGraphStatus status)
			throws PipelineGraphManipulatorException, PipelineGraphStatusException {

		LOG.info(format("cleaning dirty db after moving graph to clean db started", status));
		manipulator.deleteGraphsInDirtyDB();
		manipulator.deleteInputFile();
		status.setNoDirtyState(GraphStates.FINISHED);
		LOG.info(format("pipeline for graph successfully finished", status));
	}

	private void executeStateDirty(PipelineGraphManipulator manipulator, PipelineGraphStatus status)
			throws PipelineGraphManipulatorException, PipelineGraphStatusException {

		LOG.info(format("cleaning dirty graph started", status));
		manipulator.deleteGraphsInDirtyDB();
		status.setNoDirtyState(GraphStates.WRONG);
		LOG.info(format("cleaning dirty graph successfully finished, graph moved to WRONG state", status));
	}
	
	private String format(String message, PipelineGraphStatus status) {
		return FormatHelper.formatGraphMessage(message, status.getUuid());
	}
}
