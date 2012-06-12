package cz.cuni.mff.odcleanstore.engine;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;
import cz.cuni.mff.odcleanstore.engine.common.Module;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.inputws.InputWSService;
import cz.cuni.mff.odcleanstore.engine.outputws.OutputWSService;
import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;

/**
 * @author Petr Jerman
 */
public final class Engine extends Module {
	
	public static final String DEFAULT_CONFIG_FILE_PATH = "odcs.ini"; // TODO: Added by JM

	// parameters
	private static final String CLEAN_DATABASE_CONNECTION_STRING = "jdbc:virtuoso://localhost:1111";
	private static final String DIRTY_DATABASE_CONNECTION_STRING = "jdbc:virtuoso://localhost:1112";
	private static final String SPARQL_PASSWORD = "dba";
	private static final String SPARQL_USER = "dba";

	public static final String DATA_PREFIX = "http://opendata.cz/infrastructure/odcleanstore/";
	public static final String METADATA_PREFIX = "http://opendata.cz/infrastructure/odcleanstore/metadata/";

	public static final String INPUTWS_DIR = "inputWS/";
	public static final String INPUTWS_ENDPOINT_URL = "http://localhost:8088/odcleanstore/scraper";

	public static final int OUTPUTWS_PORT = 8087;
	public static final String OUTPUTWS_KEYWORD_PATH = "keyword";
	public static final String OUTPUTWS_URI_PATH = "uri";
	// end parameters

	public static final ConnectionCredentials CLEAN_DATABASE_ENDPOINT = new ConnectionCredentials(CLEAN_DATABASE_CONNECTION_STRING, SPARQL_USER, SPARQL_PASSWORD);
	public static final ConnectionCredentials DIRTY_DATABASE_ENDPOINT = new ConnectionCredentials(DIRTY_DATABASE_CONNECTION_STRING, SPARQL_USER, SPARQL_PASSWORD);

	private static final Logger LOG = Logger.getLogger(Engine.class);
	private static Engine _engine;

	public static void main(String[] args) {
		// TODO: Added by JM:
		try {
			if (args.length > 0) {
				ConfigLoader.loadConfig(args[0]);
			} else {
				ConfigLoader.loadConfig();
			}
		} catch (ConfigurationException e) {
			LOG.fatal(e.getMessage());
			return;
		}
		// End of added by JM
		
		if (_engine == null) {
			_engine = new Engine();
			_engine.run();
		}
	}

	private ScheduledThreadPoolExecutor _executor;

	private PipelineService _pipelineService;
	private InputWSService _inputWSService;
	private OutputWSService _userService;

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

	private void checkRequired() throws EngineException {
		try {
			File file = new File(INPUTWS_DIR);
			if (!file.exists()) {
				file.mkdir();
			}

			if (!file.isDirectory()) {
				throw new EngineException(String.format(" Directory %s not exists", INPUTWS_DIR));
			}

			if (!file.canRead()) {
				throw new EngineException(String.format(" Cannot read from inputws directory %s", INPUTWS_DIR));
			}

			if (!file.canWrite()) {
				throw new EngineException(String.format(" Cannot write to inputws directory %s", INPUTWS_DIR));
			}
		} catch (EngineException e) {
			throw e;
		} catch (Exception e) {
			throw new EngineException(e);
		}
	}

	private void init() throws EngineException {
						
		checkJavaVersion();
		checkRequired();

		_executor = new ScheduledThreadPoolExecutor(5);

		_userService = new OutputWSService(this);
		_inputWSService = new InputWSService(this);
		_pipelineService = new PipelineService(this);
	}

	private void run() {
		try {
			setModuleState(ModuleState.INITIALIZING);
			LOG.info("Engine initializing");
			init();
			setModuleState(ModuleState.RUNNING);
			LOG.info("Engine running - start services");
			startServices();
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
			String message = String.format("Engine crashed - %s", e.getMessage());
			LOG.fatal(message);
		}
	}

	private void startServices() {
		_executor.execute(_userService);
		_executor.execute(_inputWSService);
		_executor.execute(_pipelineService);

		_executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				signalToPipelineService();
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	void onServiceStateChanged(Service service) {
	}

	public static void signalToPipelineService() {
		if (_engine != null && _engine._pipelineService != null) {
			_engine._pipelineService.signalInput();
		}
	}
}
