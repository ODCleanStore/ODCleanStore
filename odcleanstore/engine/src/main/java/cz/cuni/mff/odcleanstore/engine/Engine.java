package cz.cuni.mff.odcleanstore.engine;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.engine.inputws.InputWSService;
import cz.cuni.mff.odcleanstore.engine.outputws.OutputWSService;
import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;

/**
 * @author Petr Jerman
 */
public final class Engine {

	private static final Logger LOG = Logger.getLogger(Engine.class);
	private static Engine engine;

	private ScheduledThreadPoolExecutor executor;

	private PipelineService pipelineService;
	private InputWSService inputWSService;
	private OutputWSService outputWSService;
	
	private boolean canRunDecision;
	private boolean shutdownIsInitiated;
	private Object startupLock;
	private Object shutdownLock;
	
	private String cleanDBImportExportDir;
	private String dirtyDBImportExportDir;
	
	public static void main(String[] args) {
		if (engine == null) {
			engine = new Engine();
			engine.run(args);
		}
	}

	private Engine() {
	}

	private void checkJavaVersion() throws EngineException {
		String version = System.getProperty("java.version");
		int pos = 0, count = 0;
		for (; pos < version.length() && count < 2; pos++) {
			if (version.charAt(pos) == '.')
				count++;
		}

		if (Double.parseDouble(version.substring(0, pos - 1)) < 1.6) {
			throw new EngineException("JavaRuntimeEnvironment must be 1.6 or above");
		}
	}
	
	private void loadConfiguration(String[] args) throws Exception {
			if (args.length > 0) {
				ConfigLoader.loadConfig(args[0]);
			} else {
				ConfigLoader.loadConfig();
			}
			cleanDBImportExportDir =  ConfigLoader.getConfig().getEngineGroup().getCleanImportExportDir();
			dirtyDBImportExportDir =  ConfigLoader.getConfig().getEngineGroup().getDirtyImportExportDir();
	}

	private void init(String[] args) throws Exception {
		checkJavaVersion();
		loadConfiguration(args);
		
		canRunDecision = false;
		shutdownIsInitiated = false;
		startupLock = new Object();
		shutdownLock = new Object();
		
		executor = new ScheduledThreadPoolExecutor(5);

		outputWSService = new OutputWSService(this);
		inputWSService = new InputWSService(this);
		pipelineService = new PipelineService(this);
	}

	private void run(String[] args) {
		try {
			LOG.info("Engine initializing");
			init(args);
			executor.execute(outputWSService);
			executor.execute(inputWSService);
			executor.execute(pipelineService);
			synchronized(startupLock) {
				if (isAnyServiceNewOrInitializing()) {
					startupLock.wait(ConfigLoader.getConfig().getEngineGroup().getStartupTimeout());
				}
				if(canRunDecision == false) {
					LOG.info("Not all services initialized");
					startupLock.notifyAll();
					shutdown();
				}
			}
			LOG.info("Engine running");
		} catch (Exception e) {
			LOG.fatal(FormatHelper.formatExceptionForLog(e, "Engine crashed"));
			shutdown();
		}
	}

	private void shutdown() {
		try {
			synchronized(shutdownLock) {
				if(shutdownIsInitiated) {
					return;
				}
				shutdownIsInitiated = true;
			}
			LOG.info("Engine initiate shutdown");
			if (inputWSService != null) {
				inputWSService.initiateShutdown(executor);
			}
			if (outputWSService != null) {
				outputWSService.initiateShutdown(executor);
			}
			if (pipelineService != null) {
				pipelineService.initiateShutdown(executor);
			}
			synchronized(shutdownLock) {
				if (!isAllServiceEnded()) {
					
					shutdownLock.wait(ConfigLoader.getConfig().getEngineGroup().getShutdownTimeout());
				}
				if (isAllServiceStopped()) {
					LOG.info("Engine properly shutdown");
				} else {
					LOG.info("Engine shutdown, but not all services properly shutdown");
				}
				LogManager.shutdown();
				System.exit(0);
			}
		} catch (Exception e) {
			try {
				LOG.fatal(FormatHelper.formatExceptionForLog(e, "Engine shutdown crashed"));
				LogManager.shutdown();
			} finally {
				System.exit(0);
			}
		}
	}

	void onServiceStateChanged(Service service, ServiceState oldState) {
		if (oldState.isNewOrInitializing() && !service.getServiceState().isNewOrInitializing()) {
			synchronized(startupLock) {
				 if (!isAnyServiceNewOrInitializing()) {
					 canRunDecision = isAllServiceInitialized();
					 startupLock.notifyAll();
				 }
			}
			
		} else if(!oldState.isEnded() && service.getServiceState().isEnded()) {
			synchronized(shutdownLock) {
				 if (isAllServiceEnded()) {
					 shutdownLock.notifyAll();
				 }
			}
		}
	}
	
	boolean waitForCanRunDecision() throws InterruptedException {
		synchronized(startupLock) {
			if (isAnyServiceNewOrInitializing()) {
				startupLock.wait();
			}
			return canRunDecision;
		}
	}
	
	private boolean isAnyServiceNewOrInitializing() {
		return pipelineService.getServiceState().isNewOrInitializing() ||
			   inputWSService.getServiceState().isNewOrInitializing() ||
			   outputWSService.getServiceState().isNewOrInitializing(); 
	}
	
	private boolean isAllServiceInitialized() {
		return pipelineService.getServiceState() == ServiceState.INITIALIZED &&
			   inputWSService.getServiceState() == ServiceState.INITIALIZED &&
			   outputWSService.getServiceState() == ServiceState.INITIALIZED; 
	}
	
	private boolean isAllServiceEnded() {
		return pipelineService.getServiceState().isEnded() &&
			   inputWSService.getServiceState().isEnded() &&
			   outputWSService.getServiceState().isEnded(); 
	}
	
	private boolean isAllServiceStopped() {
		return pipelineService.getServiceState() == ServiceState.STOPPED &&
			   inputWSService.getServiceState() == ServiceState.STOPPED &&
			   outputWSService.getServiceState() == ServiceState.STOPPED;  
	}
	
	
	/**
	 * @return Current instance of engine.
	 */
	public static Engine getCurrent() {
		return engine; 
	}

	public void signalToPipelineService() {
		if (engine != null && engine.pipelineService != null) {
			engine.pipelineService.notifyAboutGraphForPipeline();
		}
	}
		
	public String getEngineUuid() {
		return "88888888-8888-8888-8888-888888888888";
	}

	/**
	 * @return Path for import and export files of clean db instance.
	 * @throws EngineException 
	 */
	public String getCleanDBImportExportDir() throws EngineException {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createConnection(
					ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
			
			WrappedResultSet resultSet = con.executeSelect("SELECT server_root()");
			resultSet.next();
			String path  = Utils.satisfyDirectory(cleanDBImportExportDir, resultSet.getString(1)) + File.separator;
			return path.replace("\\", "/");
		} catch (Exception e) {
			throw new EngineException(e);
		} finally {
			if (con != null) {
				con.closeQuietly();
			}
		}
	}
	
	/**
	 * @return Path for import and export files of dirty db instance.
	 * @throws EngineException 
	 * @throws ConnectionException 
	 */
	public String getDirtyDBImportExportDir() throws EngineException {
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createConnection(
					ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
			
			WrappedResultSet resultSet = con.executeSelect("SELECT server_root()");
			resultSet.next();
			String path =  Utils.satisfyDirectory(dirtyDBImportExportDir, resultSet.getString(1)) + File.separator;
			return path.replace("\\", "/");
		} catch (Exception e) {
			throw new EngineException(e);
		} finally {
			if (con != null) {
				con.closeQuietly();
			}
		}
	}
}
