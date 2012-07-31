package cz.cuni.mff.odcleanstore.engine;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.engine.common.Module;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.inputws.InputWSService;
import cz.cuni.mff.odcleanstore.engine.outputws.OutputWSService;
import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;

/**
 * @author Petr Jerman
 */
public final class Engine extends Module {

	private static final Logger LOG = Logger.getLogger(Engine.class);
	private static Engine _engine;

	public static void main(String[] args) {
		if (_engine == null) {
			_engine = new Engine();
			_engine.run(args);
		}
	}

	private ScheduledThreadPoolExecutor _executor;

	private PipelineService _pipelineService;
	private InputWSService _inputWSService;
	private OutputWSService _outputWSService;

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
	}

	private void init(String[] args) throws Exception {
		checkJavaVersion();
		loadConfiguration(args);
	
		_executor = new ScheduledThreadPoolExecutor(5);

		_outputWSService = new OutputWSService(this);
		_inputWSService = new InputWSService(this);
		_pipelineService = new PipelineService(this);
	}

	private void run(String[] args) {
		try {
			setModuleState(ModuleState.INITIALIZING);
			LOG.info("Engine initializing");
			init(args);
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
		_executor.execute(_outputWSService);
		_executor.execute(_pipelineService);
		_executor.execute(_inputWSService);

		_executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				signalToPipelineService();
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
	
	@SuppressWarnings("unused")
	private void shutdown() {
		_executor.shutdown();
		_inputWSService.shutdown();
		_pipelineService.shutdown();
		_outputWSService.shutdown();
		LOG.info("Engine shutdown");
		LogManager.shutdown();
		System.exit(0);
	}

	void onServiceStateChanged(Service service) {
	}

	public static void signalToPipelineService() {
		if (_engine != null && _engine._pipelineService != null) {
			_engine._pipelineService.signalInput();
		}
	}
}
