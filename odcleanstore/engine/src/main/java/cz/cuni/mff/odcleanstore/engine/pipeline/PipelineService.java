package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.db.model.GraphStates;
import cz.cuni.mff.odcleanstore.engine.db.model.PipelineErrorTypes;

/**
 *  @author Petr Jerman
 */
public final class PipelineService extends Service implements Runnable {

	private static final Logger LOG = Logger.getLogger(PipelineService.class);
	
	private Object waitForGraphLock;
	private Set<PipelineGraphTransformerExecutor> activeTransformerExecutors;
	
	public PipelineService(Engine engine) {
		super(engine);
		this.waitForGraphLock = new Object();
		this.activeTransformerExecutors = Collections.synchronizedSet(new HashSet<PipelineGraphTransformerExecutor>());
	}
	
	@Override
	public void shutdown() {
		try {
		}
		catch(Exception e) {}
	}

	public void signalGraphForPipeline() {
		synchronized (waitForGraphLock) {
			waitForGraphLock.notify();
		}
	}
	
	private PipelineGraphStatus waitForGraphForPipeline() throws PipelineGraphStatusException, InterruptedException {
		synchronized (waitForGraphLock) {
			PipelineGraphStatus pipelineGraphStatus = null;
			while ((pipelineGraphStatus = PipelineGraphStatus.getNextGraphForPipeline(engineUuid)) == null) {
				waitForGraphLock.wait();
			}
			return pipelineGraphStatus;
		}
	}
	
	@Override
	public void run() {
		LOG.info("PipelineService initializing");
		setModuleState(ModuleState.INITIALIZING);
		long _waitPenalty = 0;
		while (true) {
			try {
				if (_waitPenalty > 1) {
					// TODO to global config
					Thread.sleep(80000);
				}
				LOG.info("PipelineService running");
				setModuleState(ModuleState.RUNNING);
				
				PipelineGraphStatus status = null;				
				while ((status = waitForGraphForPipeline()) != null) {
					executePipeline(status);
				}
				_waitPenalty = 0;
			} catch (Exception e) {
				// TODO: try catch this
					_waitPenalty++;
					setModuleState(ModuleState.CRASHED);
					LOG.error(FormatHelper.formatExceptionForLog(e, "PipelineService crashed"));
			}
		}
	}
	
	private void executePipeline(PipelineGraphStatus status) 
			throws PipelineGraphManipulatorException, PipelineGraphTransformerExecutorException, PipelineGraphStatusException {
		
		PipelineGraphManipulator manipulator = new PipelineGraphManipulator(status); 
		while (true) {
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
			String message = FormatHelper.formatExceptionForLog(e, format("data loading failure", status));
			status.setDirtyState(PipelineErrorTypes.DATA_LOADING_FAILURE, message);
			throw e; 		
		}
	}
	
	private void executeStateProcessingTransformers(PipelineGraphStatus status)
			throws PipelineGraphTransformerExecutorException, PipelineGraphStatusException  {

		PipelineGraphTransformerExecutor executor = null;
		try {
			executor = new PipelineGraphTransformerExecutor(status);
			this.activeTransformerExecutors.add(executor);
			executor.execute();
		} catch (PipelineGraphTransformerExecutorException e) {
			String message = FormatHelper.formatExceptionForLog(e, format("transformer processing failure", status));
			status.setDirtyState(PipelineErrorTypes.TRANSFORMER_FAILURE, message);
			throw e;
		}
		finally {
			if (executor != null) {
				this.activeTransformerExecutors.remove(executor);
			}
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
