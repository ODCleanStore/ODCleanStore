/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;

/**
 * Ancestor of services running in engine.
 * 
 *  @author Petr Jerman
 */
public abstract class Service implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(Service.class);
    
    protected final Engine engine;
    protected final String serviceName;
    private ServiceState state;
    
    /**
     * Create service instance.
     * 
     * @param engine engine context
     * @param serviceName name of service
     */
    protected Service(Engine engine, String serviceName) {
        if (engine == null || serviceName == null) {
            throw new IllegalArgumentException();
        }
        this.engine = engine;
        this.serviceName = serviceName;
        this.state = ServiceState.NEW;
    }

    /**
     * @return service current state
     */
    public final ServiceState getServiceState() {
        return this.state;
    }
    
    /**
     * @return get service current info
     */
    public abstract String getServiceStateInfo();

    private void setServiceState(ServiceState state) {
        ServiceState oldState = this.state;
        this.state = state; 
        engine.onServiceStateChanged(this, oldState);
    }
    
    /**
     * Main service routine.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            LOG.info(format("initializing"));
            setServiceState(ServiceState.INITIALIZING);
            initialize();
            setServiceState(ServiceState.INITIALIZED);
            LOG.info(format("initialized"));
            if (engine.waitForCanRunDecision()) {
                LOG.info(format("running"));
                setServiceState(ServiceState.RUNNING);
                execute();
            }
        } catch (Exception e) {
            LOG.error(FormatHelper.formatExceptionForLog(e, format("crashed")));
            setServiceState(ServiceState.CRASHED);
        }
    }
    
    /**
     * Signaling and starting shutdown.
     * 
     * @param executor thread executor for free purpose 
     */
    public final void initiateShutdown(ScheduledThreadPoolExecutor executor) {
        try {
            if (getServiceState().isForInitiateShutdown()) {
                setServiceState(ServiceState.STOP_PENDING);
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LOG.info(format("shutdown initiated"));
                            shutdown();
                            LOG.info(format("shutdown completed"));
                            setServiceState(ServiceState.STOPPED);
                        } catch (Exception e) {
                            LOG.error(FormatHelper.formatExceptionForLog(e, format("shutdown crashed")));
                            setServiceState(ServiceState.CRASHED);
                        }
                    }
                });
            }
        } catch (Exception e) {
            LOG.error(FormatHelper.formatExceptionForLog(e, format("shutdown crashed")));
            setServiceState(ServiceState.CRASHED);
        }
    }
    
    /**
     * Test if service is running and database is available.
     * 
     * @param includeDirty test dirty database included
     * @return result of test  
     */
    public final boolean isRunnningAndDbInstancesAvailable(boolean includeDirty) {
        try {
            if (getServiceState() != ServiceState.RUNNING) {
                return false;
            }

            VirtuosoConnectionWrapper con = VirtuosoConnectionWrapper.createConnection(
               ConfigLoader.getConfig().getEngineGroup().getCleanDBJDBCConnectionCredentials());
            con.closeQuietly();
            if (includeDirty) {
                con = VirtuosoConnectionWrapper.createConnection(
                        ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials());
                con.closeQuietly();
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Initialize routine for overriding.
     * 
     * @throws Exception
     */
    protected void initialize() throws Exception {
    }

    /**
     * Execute routine for overriding.
     * 
     * @throws Exception
     */    
    protected void execute() throws Exception {
    }

    /**
     * Shutdown routine for overriding.
     * 
     * @throws Exception
     */
    protected abstract void shutdown() throws Exception; 
    
    private String format(String message) {
        return serviceName + " - " + message;
    }
}