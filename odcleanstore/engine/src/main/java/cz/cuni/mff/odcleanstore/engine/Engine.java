/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ScraperService;
import cz.cuni.mff.odcleanstore.engine.ws.user.UserService;

/**
 * @author jermanp
 * 
 */
public class Engine extends Module {

	private static Engine _engine = null;

	public static void main(String[] args) {
			_engine = new Engine();
			_engine.run();
	}

	private ScheduledThreadPoolExecutor _executor;

	private PipelineService _pipelineService;
	private ScraperService _scraperService;
	private UserService _userService;

	private Engine() {
		super(null);
	}

	@Override
	public void run() {
		try {
			if (get_moduleState() != ModuleState.NEW) {
				return;
			}
			set_moduleState(ModuleState.INITIALIZING);
			init();
			set_moduleState(ModuleState.RUNNING);
			startServices();
		} catch (Exception e) {
			set_moduleState(ModuleState.CRASHED);
		}
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
		_userService = new UserService(this);
		_pipelineService = new PipelineService(this);
	}

	private void startServices() throws InterruptedException {
		_executor.execute(_scraperService);
		_executor.execute(_userService);
		_executor.execute(_pipelineService);
	}
}
