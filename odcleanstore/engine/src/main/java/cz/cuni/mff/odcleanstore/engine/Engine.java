/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.*;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.engine.common.Module;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ScraperService;
import cz.cuni.mff.odcleanstore.engine.ws.user.UserService;

/**
 * @author jermanp
 * 
 */
public final class Engine extends Module {

	// parameters
	private static final String CLEAN_DATABASE_CONNECTION_STRING = "jdbc:virtuoso://localhost:1111";
	private static final String DIRTY_DATABASE_CONNECTION_STRING = "jdbc:virtuoso://localhost:1112";
	private static final String SPARQL_PASSWORD = "dba";
	private static final String SPARQL_USER = "dba";

	public static final String DATA_PREFIX = "http://opendata.cz/infrastructure/odcleanstore/";
	public static final String METADATA_PREFIX = "http://opendata.cz/infrastructure/odcleanstore/metadata/";

	public static final String SCRAPER_INPUT_DIR = "C:/odcs/ScraperInput/";

	public static final String SCRAPER_ENDPOINT_URL = "http://localhost:8088/odcleanstore/scraper";
	public static final int USER_SERVICE_PORT = 8087;
	public static final String USER_SERVICE_KEYWORD_PATH = "keyword";
	public static final String USER_SERVICE_URI_PATH = "uri";
	// end parameters

	public static final SparqlEndpoint CLEAN_DATABASE_ENDPOINT = new SparqlEndpoint(CLEAN_DATABASE_CONNECTION_STRING, SPARQL_USER, SPARQL_PASSWORD);
	public static final SparqlEndpoint DIRTY_DATABASE_ENDPOINT = new SparqlEndpoint(DIRTY_DATABASE_CONNECTION_STRING, SPARQL_USER, SPARQL_PASSWORD);

	private static final Logger LOG = Logger.getLogger(Engine.class);
	private static Engine _engine;

	public static void main(String[] args) {
		if (_engine == null) {
			_engine = new Engine();
			_engine.run();
		}
	}

	private ScheduledThreadPoolExecutor _executor;

	private PipelineService _pipelineService;
	private ScraperService _scraperService;
	private UserService _userService;

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
			File file = new File(SCRAPER_INPUT_DIR);
			if (!file.exists()) {
				file.mkdir();
			}

			if (!file.isDirectory()) {
				throw new EngineException(String.format(" Directory %s not exists", SCRAPER_INPUT_DIR));
			}

			if (!file.canRead()) {
				throw new EngineException(String.format(" Cannot read from scraper input directory %s", SCRAPER_INPUT_DIR));
			}

			if (!file.canWrite()) {
				throw new EngineException(String.format(" Cannot write to scraper input directory %s", SCRAPER_INPUT_DIR));
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

		_userService = new UserService(this);
		_scraperService = new ScraperService(this);
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
		_executor.execute(_scraperService);
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
