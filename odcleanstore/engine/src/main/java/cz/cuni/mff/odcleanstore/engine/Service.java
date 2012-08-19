/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;

/**
 *  @author Petr Jerman
 */
public abstract class Service implements Runnable {
	
	private static final Logger LOG = Logger.getLogger(Service.class);
	
	protected final Engine engine;
	protected final String serviceName;
	private ServiceState state;
	
	protected Service(Engine engine, String serviceName) {
		if (engine == null || serviceName == null) {
			throw new IllegalArgumentException();
		}
		this.engine = engine;
		this.serviceName = serviceName;
		this.state = ServiceState.NEW;
	}

	public final ServiceState getServiceState() {
		return this.state;
	}

	private void setServiceState(ServiceState state) {
		ServiceState oldState = this.state;
		this.state = state; 
		engine.onServiceStateChanged(this, oldState);
	}
	
	@Override
	public void run() {
		try {
			LOG.info(format("initializing"));
			setServiceState(ServiceState.INITIALIZING);
			initialize();
			setServiceState(ServiceState.INITIALIZED);
			LOG.info(format("initialized"));
			if (engine.waitForCanRunDecision())
			{
				LOG.info(format("running"));
				setServiceState(ServiceState.RUNNING);
				execute();
			}
		} catch (Exception e) {
			LOG.fatal(FormatHelper.formatExceptionForLog(e, format("crashed")));
			setServiceState(ServiceState.CRASHED);
		}
	}
	
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
						} catch(Exception e) {
							LOG.fatal(FormatHelper.formatExceptionForLog(e, format("shutdown crashed")));
							setServiceState(ServiceState.CRASHED);
						}
					}});
			}
		} catch (Exception e) {
			LOG.fatal(FormatHelper.formatExceptionForLog(e, format("shutdown crashed")));
			setServiceState(ServiceState.CRASHED);
		}
	}
	
	protected void initialize() throws Exception {}
	protected void execute() throws Exception {}	
	protected abstract void shutdown() throws Exception; 
	
	private String format(String message) {
		return serviceName + " - " + message;
	}
}