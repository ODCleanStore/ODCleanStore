/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.engine.common.Module;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ScraperService;
import cz.cuni.mff.odcleanstore.engine.ws.user.UserService;

/**
 * @author jermanp
 * 
 */
public final class Engine extends Module {

	public static final String DATA_PREFIX = "http://d/";
	public static final String METADATA_PREFIX = "http://m/";
	public static final SparqlEndpoint DIRTY_DATABASE_ENDPOINT = new SparqlEndpoint("","",""); 
	public static final SparqlEndpoint CLEAN_DATABASE_ENDPOINT = new SparqlEndpoint("","","");
	public static final String SCRAPER_ENDPOINT_URL = "http://localhost:8088/odcleanstore/scraper";
	public static final int USER_SERVICE_PORT = 8087;
	public static final String USER_SERVICE_KEYWORD_PATH = "keyword";
	public static final String USER_SERVICE_URI_PATH = "uri";
	
	public static void main(String[] args) {
		_engine = new Engine();
		_engine.run();
	}

	private ScheduledThreadPoolExecutor _executor;

	private PipelineService _pipelineService;
	private ScraperService _scraperService;
	private UserService _userService;

	private static Engine _engine;

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

	private void init() throws EngineException {
		checkJavaVersion();

		_executor = new ScheduledThreadPoolExecutor(1);

		_scraperService = new ScraperService(this);
		_pipelineService = new PipelineService(this);
		_userService = new UserService(this);
	}

	private void run() {
		try {
			setModuleState(ModuleState.INITIALIZING);
			init();
			setModuleState(ModuleState.RUNNING); // TODO: JM: nemelo by byt az po startServices()?
			startServices();
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
		}
	}

	private void startServices() {
		_executor.execute(_scraperService);
		_executor.execute(_pipelineService);
		_executor.execute(_userService);
	}



	static PipelineService getPipelineService() {
		return _engine._pipelineService;
	}

	static ScraperService getScraperService() {
		return _engine._scraperService;
	}

	static UserService getUserService() {
		return _engine._userService;
	}

	static void onServiceStateChanged(Service service) {
	}
}
