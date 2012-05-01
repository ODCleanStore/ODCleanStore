package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.common.SimpleVirtuosoAccess;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Metadata;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

public final class PipelineService extends Service implements Runnable {

	private WorkingInputGraphStatus _workingInputGraphStatus;
	private WorkingInputGraph _workingInputGraph;

	public PipelineService(Engine engine) {
		super(engine);
	}

	private Object fromInputWSLocks = new Object();

	public void signalInput() {
		synchronized (fromInputWSLocks) {
			fromInputWSLocks.notify();
		}
	}

	private String waitForInput() {
		synchronized (fromInputWSLocks) {
			try {
				String uuid;
				for (uuid = _workingInputGraphStatus.getNextProcessingGraphUuid(); uuid == null; uuid = _workingInputGraphStatus.getNextProcessingGraphUuid()) {
					fromInputWSLocks.wait();
				}
				return uuid;
			} catch (Exception e) {
				return null;
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				synchronized (this) {
					if (getModuleState() != ModuleState.NEW && getModuleState() != ModuleState.CRASHED) {
						return;
					}
					setModuleState(ModuleState.INITIALIZING);
				}

				_workingInputGraphStatus = new WorkingInputGraphStatus("DB.FRONTEND");
				_workingInputGraph = new WorkingInputGraph();

				String graphsForRecoveryUuid = _workingInputGraphStatus.getWorkingTransformedGraphUuid();
				if (graphsForRecoveryUuid != null) {
					setModuleState(ModuleState.RECOVERY);
					recovery(graphsForRecoveryUuid);
				}
				setModuleState(ModuleState.RUNNING);
				runPipeline();
				setModuleState(ModuleState.STOPPED);
			} catch (Exception e) {
				_workingInputGraphStatus.setWorkingTransformedGraph(null);
				setModuleState(ModuleState.CRASHED);
			}
		}
	}

	private void recovery(String uuid) throws Exception {

		InputGraphState state = _workingInputGraphStatus.getState(uuid);

		switch (state) {
		case PROCESSING:
			_workingInputGraph.deleteGraphsFromDirtyDB(_workingInputGraphStatus.getWorkingAttachedGraphNames());
			_workingInputGraph.deleteGraphFromDirtyDB(Engine.DATA_PREFIX + uuid);
			_workingInputGraph.deleteGraphFromDirtyDB(Engine.METADATA_PREFIX + uuid);

			_workingInputGraphStatus.deleteWorkingAttachedGraphNames();
			_workingInputGraphStatus.setState(uuid, InputGraphState.IMPORTED);
			break;
		case PROCESSED:
			processProcessedState(uuid);
		case PROPAGATED:
			processPropagatedState(uuid);
			break;
		case DELETING:
			processDeletingState(uuid);
			break;
		case DIRTY:
			_workingInputGraph.deleteGraphsFromDirtyDB(_workingInputGraphStatus.getWorkingAttachedGraphNames());
			_workingInputGraph.deleteGraphFromDirtyDB(Engine.DATA_PREFIX + uuid);
			_workingInputGraph.deleteGraphFromDirtyDB(Engine.METADATA_PREFIX + uuid);

			_workingInputGraphStatus.deleteWorkingAttachedGraphNames();
			_workingInputGraphStatus.setState(uuid, InputGraphState.WRONG);
			break;
		}
	}

	private void runPipeline() throws Exception {

		TransformedGraphImpl transformedGraphImpl = null;
		String uuid = null;

		while ((uuid = waitForInput()) != null) {
			try {
				Collection<TransformerCommand> TransformerCommands = TransformerCommand.getActualPlan("DB.FRONTEND");
				loadData(uuid);

				for (TransformerCommand transformerCommand : TransformerCommands) {
					transformedGraphImpl = transformedGraphImpl == null ? new TransformedGraphImpl(_workingInputGraphStatus, uuid) : new TransformedGraphImpl(transformedGraphImpl);
					processTransformer(transformerCommand, transformedGraphImpl);
					if (transformedGraphImpl.isDeleted()) {
						break;
					}

				}
			} catch (Exception e) {
				_workingInputGraphStatus.setWorkingTransformedGraph(null);
				_workingInputGraphStatus.setState(uuid, InputGraphState.DIRTY);
				throw e;
			}

			if (transformedGraphImpl.isDeleted()) {
				processDeletingState(uuid);
			} else {
				_workingInputGraphStatus.setState(uuid, InputGraphState.PROCESSED);
				processProcessedState(uuid);
				processPropagatedState(uuid);
			}
		}
	}

	private void loadData(String uuid) throws Exception {
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		String inserted = null;
		Metadata metadata = null;
		String rdfXmlPayload = null;
		try {
			fin = new FileInputStream(Engine.SCRAPER_INPUT_DIR + uuid + ".dat");
			ois = new ObjectInputStream(fin);
			inserted = (String) ois.readObject();
			metadata = (Metadata) ois.readObject();
			rdfXmlPayload = (String) ois.readObject();
		} finally {
			if (ois != null) {
				ois.close();
			}
			if (fin != null) {
				fin.close();
			}
		}

		SimpleVirtuosoAccess sva = null;
		try {
			sva = SimpleVirtuosoAccess.createDirtyDBConnection();

			sva.insertQuad("<" + Engine.DATA_PREFIX + uuid + ">", "<" + W3P.insertedAt + ">", inserted, "<" + Engine.METADATA_PREFIX + uuid + ">");
			sva.insertQuad("<" + Engine.DATA_PREFIX + uuid + ">", "<" + W3P.insertedBy + ">", "'scraper'", "<" + Engine.METADATA_PREFIX + uuid + ">");
			for (String source : metadata.source) {
				sva.insertQuad("<" + Engine.DATA_PREFIX + uuid + ">", "<" + W3P.source + ">", "<" + source + ">", "<" + Engine.METADATA_PREFIX + uuid + ">");
			}
			for (String publishedBy : metadata.publishedBy) {
				sva.insertQuad("<" + Engine.DATA_PREFIX + uuid + ">", "<" + W3P.publishedBy + ">", "<" + publishedBy + ">", "<" + Engine.METADATA_PREFIX + uuid + ">");
			}
			if (metadata.license != null) {
				for (String license : metadata.license) {
					sva.insertQuad("<" + Engine.DATA_PREFIX + uuid + ">", "<" + DC.license + ">", "<" + license + ">", "<" + Engine.METADATA_PREFIX + uuid + ">");
				}
			}

			sva.insertRdfXml(metadata.dataBaseUrl, rdfXmlPayload, Engine.DATA_PREFIX + uuid);
			sva.commit();
		} finally {
			if (sva != null) {
				sva.close();
			}
		}
	}

	private void processTransformer(TransformerCommand transformerCommand, TransformedGraphImpl transformedGraphImpl) throws Exception {
		if (!transformerCommand.getJarPath().equals(".")) {
			throw new PipelineException("Prototype - Custom transformers not supported");
		}

		Transformer transformer = null;

		if (transformerCommand.getFullClassName().equals("cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl")) {
			transformer = new cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl();
		} else if (transformerCommand.getFullClassName().equals("cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl")) {
			transformer = new cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl();
		}

		if (transformer != null) {
			TransformationContextImpl context = new TransformationContextImpl(transformerCommand.getConfiguration(), transformerCommand.getWorkDirPath());

			_workingInputGraphStatus.setWorkingTransformedGraph(transformedGraphImpl);
			transformer.transformNewGraph(transformedGraphImpl, context);
			_workingInputGraphStatus.setWorkingTransformedGraph(null);
		} else {
			// throw new PipelineException("Prototype - Unknown transformer");
		}
	}

	private void processDeletingState(String uuid) throws Exception {
		_workingInputGraph.deleteGraphsFromDirtyDB(_workingInputGraphStatus.getWorkingAttachedGraphNames());
		_workingInputGraph.deleteGraphFromDirtyDB(Engine.DATA_PREFIX + uuid);
		_workingInputGraph.deleteGraphFromDirtyDB(Engine.METADATA_PREFIX + uuid);

		_workingInputGraphStatus.deleteGraphAndWorkingAttachedGraphNames(uuid);
	}

	private void processProcessedState(String uuid) throws Exception {
		ArrayList<String> graphs = new ArrayList<String>();
		graphs.addAll(_workingInputGraphStatus.getWorkingAttachedGraphNames());
		graphs.add(Engine.DATA_PREFIX + uuid);
		graphs.add(Engine.METADATA_PREFIX + uuid);

		_workingInputGraph.copyGraphsFromDirtyDBToCleanDB(graphs);

		_workingInputGraphStatus.setState(uuid, InputGraphState.PROPAGATED);
	}

	private void processPropagatedState(String uuid) throws Exception {
		ArrayList<String> graphs = new ArrayList<String>();
		graphs.addAll(_workingInputGraphStatus.getWorkingAttachedGraphNames());
		graphs.add(Engine.DATA_PREFIX + uuid);
		graphs.add(Engine.METADATA_PREFIX + uuid);

		_workingInputGraph.deleteGraphsFromDirtyDB(graphs);
		File inputFile = new File(Engine.SCRAPER_INPUT_DIR + uuid + ".dat");
		inputFile.delete();

		_workingInputGraphStatus.deleteWorkingAttachedGraphNames();
		_workingInputGraphStatus.setState(uuid, InputGraphState.FINISHED);
	}
}
