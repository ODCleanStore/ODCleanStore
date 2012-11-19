package cz.cuni.mff.odcleanstore.engine;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.slf4j.bridge.SLF4JBridgeHandler;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.model.DbOdcsContext;
import cz.cuni.mff.odcleanstore.engine.inputws.InputWSService;
import cz.cuni.mff.odcleanstore.engine.outputws.OutputWSService;
import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;
import cz.cuni.mff.odcleanstore.shared.FileUtils;

/**
 * @author Petr Jerman
 */
public final class Engine {

    private static final Logger LOG = Logger.getLogger(Engine.class);
    
    private static final double MIN_JAVA_VERSION = 1.6;
    private static final int THREAD_POOL_SIZE = 5;
    
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
            if (version.charAt(pos) == '.') {
                count++;
            }
        }

        if (Double.parseDouble(version.substring(0, pos - 1)) < MIN_JAVA_VERSION) {
            throw new EngineException("JavaRuntimeEnvironment must be 1.6 or above");
        }
    }
    
    /**
     * Check and set logging configuration.
     */
    private void checkLoggingConfiguration() {
        if (!SLF4JBridgeHandler.isInstalled()) {
            SLF4JBridgeHandler.install();
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
    
    private void setShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                shutdown();
            }
        });
    }

    private void init(String[] args) throws Exception {
        checkJavaVersion();
        checkLoggingConfiguration();
        loadConfiguration(args);
        setShutdownHook();
        
        canRunDecision = false;
        shutdownIsInitiated = false;
        startupLock = new Object();
        shutdownLock = new Object();
        
        executor = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);

        outputWSService = new OutputWSService(this);
        inputWSService = new InputWSService(this);
        pipelineService = new PipelineService(this);
    }

    private void run(String[] args) {
        try {
            LOG.info("Engine initializing");
            init(args);
            updateEngineStatusToDb("Engine initializing");
            executor.execute(outputWSService);
            executor.execute(inputWSService);
            executor.execute(pipelineService);
            synchronized (startupLock) {
                if (isAnyServiceNewOrInitializing()) {
                    startupLock.wait(ConfigLoader.getConfig().getEngineGroup().getStartupTimeout());
                }
                if (!canRunDecision) {
                    LOG.info("Not all services initialized");
                    startupLock.notifyAll();
                    shutdown();
                }
            }
            LOG.info("Engine running");
            updateEngineStatusToDb("Engine running");
            long delay = ConfigLoader.getConfig().getEngineGroup().getStateToDbWritingInterval();
            executor.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					updateEngineStatusToDb();
				}
            }, delay, delay, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            LOG.fatal(FormatHelper.formatExceptionForLog(e, "Engine crashed"));
            shutdown();
        }
    }

    private void shutdown() {
        try {
            synchronized (shutdownLock) {
                if (shutdownIsInitiated) {
                    return;
                }
                shutdownIsInitiated = true;
            }
            LOG.info("Engine initiate shutdown");
            updateEngineStatusToDb("Engine initiate shutdown");
            if (inputWSService != null) {
                inputWSService.initiateShutdown(executor);
            }
            if (outputWSService != null) {
                outputWSService.initiateShutdown(executor);
            }
            if (pipelineService != null) {
                pipelineService.initiateShutdown(executor);
            }
            synchronized (shutdownLock) {
                if (!isAllServiceEnded()) {

                    shutdownLock.wait(ConfigLoader.getConfig().getEngineGroup().getShutdownTimeout());
                }
                if (isAllServiceStopped()) {
                    LOG.info("Engine properly shutdown");
                } else {
                    LOG.info("Engine shutdown, but not all services properly shutdown");
                }
                updateEngineStatusToDb("Engine is shutdown");
                LogManager.shutdown();
                Runtime.getRuntime().halt(0);
            }
        } catch (Exception e) {
            try {
                LOG.fatal(FormatHelper.formatExceptionForLog(e, "Engine shutdown crashed"));
                LogManager.shutdown();
            } finally {
                Runtime.getRuntime().halt(0);
            }
        }
    }

    void onServiceStateChanged(Service service, ServiceState oldState) {
        if (oldState.isNewOrInitializing() && !service.getServiceState().isNewOrInitializing()) {
            synchronized (startupLock) {
                if (!isAnyServiceNewOrInitializing()) {
                    canRunDecision = isAllServiceInitialized();
                    startupLock.notifyAll();
                }
            }

        } else if (!oldState.isEnded() && service.getServiceState().isEnded()) {
            synchronized (shutdownLock) {
                if (isAllServiceEnded()) {
                    shutdownLock.notifyAll();
                }
            }
        }
    }

    boolean waitForCanRunDecision() throws InterruptedException {
        synchronized (startupLock) {
            if (isAnyServiceNewOrInitializing()) {
                startupLock.wait();
            }
            return canRunDecision;
        }
    }

    private boolean isAnyServiceNewOrInitializing() {
        return pipelineService.getServiceState().isNewOrInitializing()
                || inputWSService.getServiceState().isNewOrInitializing()
                || outputWSService.getServiceState().isNewOrInitializing();
    }

    private boolean isAllServiceInitialized() {
        return pipelineService.getServiceState() == ServiceState.INITIALIZED
                && inputWSService.getServiceState() == ServiceState.INITIALIZED
                && outputWSService.getServiceState() == ServiceState.INITIALIZED;
    }

    private boolean isAllServiceEnded() {
        return pipelineService.getServiceState().isEnded()
                && inputWSService.getServiceState().isEnded()
                && outputWSService.getServiceState().isEnded();
    }

    private boolean isAllServiceStopped() {
        return pipelineService.getServiceState() == ServiceState.STOPPED
                && inputWSService.getServiceState() == ServiceState.STOPPED
                && outputWSService.getServiceState() == ServiceState.STOPPED;
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
        return ConfigLoader.getConfig().getEngineGroup().getEngineUuid().toString();
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
            String path  = FileUtils.satisfyDirectory(cleanDBImportExportDir, con.getServerRoot()) + File.separator;
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
            String path =  FileUtils.satisfyDirectory(dirtyDBImportExportDir, con.getServerRoot()) + File.separator;
            return path.replace("\\", "/");
        } catch (Exception e) {
            throw new EngineException(e);
        } finally {
            if (con != null) {
                con.closeQuietly();
            }
        }
    }
    
    private void updateEngineStatusToDb() {
    	if (shutdownIsInitiated) {
    		return;
    	}
    	StringBuilder sb = null;
    	
    	try {
    	sb = new StringBuilder();
    	sb.append(pipelineService.getServiceStateInfo());
   		sb.append("\n");
    	sb.append(inputWSService.getServiceStateInfo());
    	sb.append("\n");
   		sb.append(outputWSService.getServiceStateInfo());
    	} catch (Exception e){
    		LOG.warn("Getting dervice state info before updating engine state to db error");
    	}
    	
    	if (sb != null) {
    		updateEngineStatusToDb(sb.toString());
    	}
    }
 
    private void updateEngineStatusToDb(String stateDescription) {
    	DbOdcsContext context = null;
    	try {
    		context = new DbOdcsContext();
    		context.updateEngineState(getEngineUuid(), stateDescription);
    		context.commit();
    	} catch(Exception e) {
    		LOG.warn("Updating engine state to db error");
    	} finally {
    		if (context != null) {
    			context.closeQuietly();
    		}
    	}
    }
}
