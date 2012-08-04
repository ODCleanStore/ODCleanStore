package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.BackendConfig;
import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.InputGraphState;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.Metadata;
import cz.cuni.mff.odcleanstore.transformer.Transformer;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

/**
 *  @author Petr Jerman
 */
public final class PipelineService extends Service implements Runnable {

	private static final Logger LOG = Logger.getLogger(PipelineService.class);
	
	private TransformedGraphStatus _workingInputGraphStatus;
	private TransformedGraphManipulation _workingInputGraph;
	
	private int _pipelineWaitPenalty = 0;
	
	private Transformer _currentTransformer;

	public PipelineService(Engine engine) {
		super(engine);
	}
	
	@Override
	public void shutdown() {
		try {
			Transformer currentTransformer = _currentTransformer; 
			if (currentTransformer != null) {
				currentTransformer.shutdown();
			}
		}
		catch(Exception e) {}
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
					
					Thread.sleep(_pipelineWaitPenalty);
					// TODO pridat parametr do config
					_pipelineWaitPenalty = 30000;

					LOG.info("PipelineService initializing");
					setModuleState(ModuleState.INITIALIZING);
				}

				_workingInputGraphStatus = new TransformedGraphStatus("DB.ODCLEANSTORE");
				_workingInputGraph = new TransformedGraphManipulation();

				String graphsForRecoveryUuid = _workingInputGraphStatus.getWorkingTransformedGraphUuid();
				if (graphsForRecoveryUuid != null) {
					LOG.info("PipelineService starts recovery");
					setModuleState(ModuleState.RECOVERY);
					
					recovery(graphsForRecoveryUuid);
				}
				
				_pipelineWaitPenalty = 0;
				LOG.info("PipelineService running");
				setModuleState(ModuleState.RUNNING);
				runPipeline();
				LOG.info("PipelineService stopped");
				setModuleState(ModuleState.STOPPED);

			} catch (Exception e) {
				_workingInputGraphStatus.setWorkingTransformedGraph(null);
				String message = String.format("PipelineService crashed - %s", e.getMessage());
				LOG.error(message);
				e.printStackTrace();
				setModuleState(ModuleState.CRASHED);
			}
		}
	}
	
	private void recovery(String uuid) throws Exception {
		
		int state = _workingInputGraphStatus.getState(uuid);
		BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();

		switch (state) {
		case InputGraphState.PROCESSING:
			_workingInputGraph.deleteGraphsFromDirtyDB(_workingInputGraphStatus.getWorkingAttachedGraphNames());
			_workingInputGraph.deleteGraphFromDirtyDB(backendConfig.getDataGraphURIPrefix() + uuid);
			_workingInputGraph.deleteGraphFromDirtyDB(backendConfig.getMetadataGraphURIPrefix() + uuid);
			_workingInputGraph.deleteGraphFromDirtyDB(backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid);

			_workingInputGraphStatus.deleteWorkingAttachedGraphNames();
			_workingInputGraphStatus.setState(uuid, InputGraphState.QUEUED);
			LOG.info("PipelineService ends recovery from interrupted processing");
			break;
		case InputGraphState.PROCESSED:
			processProcessedState(uuid);
		case InputGraphState.PROPAGATED:
			processPropagatedState(uuid);
			LOG.info("PipelineService ends recovery from interrupted copying graph from dirty to clean database instance");
			break;
		case InputGraphState.QUEUED_FOR_DELETE:
			processDeletingState(uuid);
			LOG.info("PipelineService ends recovery from interrupted deleting graph");
			break;
		case InputGraphState.DIRTY:
			_workingInputGraph.deleteGraphsFromDirtyDB(_workingInputGraphStatus.getWorkingAttachedGraphNames());
			_workingInputGraph.deleteGraphFromDirtyDB(backendConfig.getDataGraphURIPrefix() + uuid);
			_workingInputGraph.deleteGraphFromDirtyDB(backendConfig.getMetadataGraphURIPrefix() + uuid);
			_workingInputGraph.deleteGraphFromDirtyDB(backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid);
			
			_workingInputGraphStatus.deleteWorkingAttachedGraphNames();
			_workingInputGraphStatus.setState(uuid, InputGraphState.WRONG);
			LOG.info("PipelineService ends recovery from crashed pipeline proccesing");
			break;
		}
	}

	private void runPipeline() throws Exception {
		String uuid = null;

		while ((uuid = waitForInput()) != null) {
			TransformedGraphImpl transformedGraphImpl = null;
			
			try {
				LOG.info(String.format("PipelineService starts processing graph %s", uuid));
				int dbKeyId = _workingInputGraphStatus.getGraphDbKeyId(uuid);
				int pipelineId = _workingInputGraphStatus.getGraphPipelineId(uuid);
				Collection<TransformerCommand> TransformerCommands = TransformerCommand.getActualPlan("DB.ODCLEANSTORE", pipelineId);
				loadData(uuid);
				LOG.info(String.format("PipelineService ends data loading for graph %s", uuid));
				for (TransformerCommand transformerCommand : TransformerCommands) {
					transformedGraphImpl = transformedGraphImpl == null ? new TransformedGraphImpl(_workingInputGraphStatus, dbKeyId, uuid) : new TransformedGraphImpl(transformedGraphImpl);
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

			if (transformedGraphImpl != null && transformedGraphImpl.isDeleted()) {
				processDeletingState(uuid);
			} else {
				_workingInputGraphStatus.setState(uuid, InputGraphState.PROCESSED);
				processProcessedState(uuid);
				processPropagatedState(uuid);
			}
		}
	}

	private void loadData(String uuid) throws Exception {
		BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();
		
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		String inserted = null;
		Metadata metadata = null;
		String payload = null;
		try {
			String inputDirPath = ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
			fin = new FileInputStream(inputDirPath + uuid + ".dat");
			ois = new ObjectInputStream(fin);
			inserted = (String) ois.readObject();
			metadata = (Metadata) ois.readObject();
			payload = (String) ois.readObject();
		} finally {
			if (ois != null) {
				ois.close();
			}
			if (fin != null) {
				fin.close();
			}
		}

		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createTransactionalLevelConnection(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
			String dataGraphURI = backendConfig.getDataGraphURIPrefix() + uuid;
			String metadataGraphURI = backendConfig.getMetadataGraphURIPrefix() + uuid;
			String provenanceGraphURI = backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid;

			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.metadataGraph + ">", "<" + metadataGraphURI + ">", "<" + metadataGraphURI + ">");
			
			con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.insertedAt + ">", inserted, "<" + metadataGraphURI + ">");
			con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.insertedBy + ">", "'scraper'", "<" + metadataGraphURI + ">");
			for (String source : metadata.source) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.source + ">", "<" + source + ">", "<" + metadataGraphURI + ">");
			}
			for (String publishedBy : metadata.publishedBy) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.publishedBy + ">", "<" + publishedBy + ">", "<" + metadataGraphURI + ">");
			}
			if (metadata.license != null) {
				for (String license : metadata.license) {
					con.insertQuad("<" + dataGraphURI + ">", "<" + DC.license + ">", "<" + license + ">", "<" + metadataGraphURI + ">");
				}
			}
			if (metadata.provenance != null) {
				con.insertRdfXmlOrTtl(metadata.dataBaseUrl, metadata.provenance, provenanceGraphURI);
				con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.provenanceMetadataGraph + ">", "<" + provenanceGraphURI + ">", "<" + metadataGraphURI + ">");
			}
			con.insertRdfXmlOrTtl(metadata.dataBaseUrl, payload, dataGraphURI);
			con.commit();
		} finally {
			if (con != null) {
				con.close();
			}
		}
	}
	
	private Transformer loadCustomTransformer(TransformerCommand transformerCommand) throws Exception {
		
		URL url = new File(transformerCommand.getJarPath()).toURI().toURL();
		URLClassLoader loader = new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
		Class<?> trida = Class.forName(transformerCommand.getFullClassName(), true, loader);
		Object obj = trida.getConstructor(new Class[]{}).newInstance(new Object[]{});
		return  obj instanceof Transformer ? (Transformer) obj : null;
	}

	private void processTransformer(TransformerCommand transformerCommand, TransformedGraphImpl transformedGraphImpl) throws Exception {
		_currentTransformer = null;
		
		if (!transformerCommand.getJarPath().equals(".")) {
			_currentTransformer = loadCustomTransformer(transformerCommand);
		}
		else {
			if (transformerCommand.getFullClassName().equals("cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl")) {
				_currentTransformer = new cz.cuni.mff.odcleanstore.linker.impl.LinkerImpl(ConfigLoader.getConfig().getObjectIdentificationConfig());
			} else if (transformerCommand.getFullClassName().equals("cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl")) {
				//TODO: This is HOTFIX. Engine needs to pass proper groupIds or groupLabels in constructor of QAImpl
				//This only makes common ids be selected (as groupId is IDENTITY (AUTOINCREMENT starting at 1))
				_currentTransformer = new cz.cuni.mff.odcleanstore.qualityassessment.impl.QualityAssessorImpl(0, 1, 2, 3, 4, 5);
			}	
		}

		if (_currentTransformer != null) {
			String path = checkTransformerWorkingDirectory(transformerCommand.getWorkDirPath());
			TransformationContextImpl context = new TransformationContextImpl(transformerCommand.getConfiguration(), path);

			_workingInputGraphStatus.setWorkingTransformedGraph(transformedGraphImpl);
			_currentTransformer.transformNewGraph(transformedGraphImpl, context);
			_currentTransformer.shutdown();
			_currentTransformer = null;
			LOG.info(String.format("PipelineService ends proccesing %s transformer on graph %s", transformerCommand.getFullClassName(), transformedGraphImpl.getGraphId()));
			_workingInputGraphStatus.setWorkingTransformedGraph(null);
		} else {
			LOG.warn(String.format("PipelineService - unknown transformer %s ignored", transformerCommand.getFullClassName()));
		}
	}
	
	private String checkTransformerWorkingDirectory(String dirName) throws PipelineException {
		try {
			return Utils.satisfyDirectory(dirName);
		} catch (Utils.DirectoryException e) {
			throw new PipelineException("Transformer working directory checking error", e);
		}
	}

	private void processDeletingState(String uuid) throws Exception {
		BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();
		ArrayList<String> graphs = new ArrayList<String>();
		graphs.addAll(_workingInputGraphStatus.getWorkingAttachedGraphNames());
		graphs.add(backendConfig.getDataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getMetadataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid);
		_workingInputGraph.deleteGraphsFromDirtyDB(graphs);
		
		String inputDirPath = ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
		File inputFile = new File(inputDirPath + uuid + ".dat");
		inputFile.delete();
		
		_workingInputGraphStatus.deleteGraphAndWorkingAttachedGraphNames(uuid);
		LOG.info(String.format("PipelineService ends deleting graph %s", uuid));
	}

	private void processProcessedState(String uuid) throws Exception {
		BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();
		ArrayList<String> graphs = new ArrayList<String>();
		graphs.addAll(_workingInputGraphStatus.getWorkingAttachedGraphNames());
		graphs.add(backendConfig.getDataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getMetadataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid);

		_workingInputGraph.copyGraphsFromDirtyDBToCleanDB(graphs);

		_workingInputGraphStatus.setState(uuid, InputGraphState.PROPAGATED);
	}

	private void processPropagatedState(String uuid) throws Exception {
		BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();
		ArrayList<String> graphs = new ArrayList<String>();
		graphs.addAll(_workingInputGraphStatus.getWorkingAttachedGraphNames());
		graphs.add(backendConfig.getDataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getMetadataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid);
		_workingInputGraph.deleteGraphsFromDirtyDB(graphs);
		
		String inputDirPath = ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
		File inputFile = new File(inputDirPath + uuid + ".dat");
		inputFile.delete();

		_workingInputGraphStatus.setState(uuid, InputGraphState.FINISHED);
		LOG.info(String.format("PipelineService has finished graph %s", uuid));
	}
}
