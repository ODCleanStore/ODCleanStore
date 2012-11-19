package cz.cuni.mff.odcleanstore.engine.inputws;

import java.net.URL;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.net.ssl.KeyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.InputWSConfig;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;

/**
 * @author Petr Jerman
 */
public final class InputWSService extends Service {

	private static final Logger LOG = LoggerFactory.getLogger(InputWSService.class);

	private InputWSHttpServer server;
	private ScheduledThreadPoolExecutor executor;

	public InputWSService(Engine engine) {
		super(engine, "InputWSService");
		executor = new ScheduledThreadPoolExecutor(8);
	}

	@Override
	public String getServiceStateInfo() {
		Date lastRecoveryDate = InsertExecutor.getLastActiveWaitingForRecoveryDate();
		if (lastRecoveryDate != null) {
			return String.format("InputWS is waiting for recovery from %s", lastRecoveryDate.toString());
		}

		if (isRunnningAndDbInstancesAvailable(false)) {
			return "InputWS is running";
		}
		
		return String.format("InputWS is not running, has %s state", getServiceState().toString());
	}

	@Override
	protected void initialize() throws Exception {

		InputWSConfig inputWSConfig = ConfigLoader.getConfig().getInputWSGroup();
		URL endpoint = inputWSConfig.getEndpointURL();
		LOG.info("InputWS - Starting the server on {}", endpoint);

		if (endpoint.getProtocol().equalsIgnoreCase("https")) {
			KeyManager[] keys = SslKeyLoader.getKeys();
			if (keys == null) {
				LOG.error("Certificate for https server loading error");
				throw new Exception();
			}
			server = new InputWSHttpServer(endpoint, keys);
		} else {
			server = new InputWSHttpServer(endpoint, null);
		}
		server.start(executor);
	}

	@Override
	protected void execute() throws Exception {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				executeInternal();
			}
		});
	}

	@Override
	public void shutdown() throws Exception {
		if (server != null) {
			server.stop();
		}
	}

	private void executeInternal() {
		InsertExecutor.recoveryOnStartup();
		server.setAvailable(true);
		LOG.info("InputWS - Server accepts incoming requests");
	}
}
