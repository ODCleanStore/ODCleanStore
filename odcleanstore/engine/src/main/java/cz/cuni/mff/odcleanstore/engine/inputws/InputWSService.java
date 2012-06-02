package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.File;
import java.util.Collection;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 *  @author Petr Jerman
 */
public final class InputWSService extends Service implements Runnable {

	private static final Logger LOG = Logger.getLogger(InputWSService.class);

	public InputWSService(Engine engine) {
		super(engine);
	}

	@Override
	public void run() {
		try {
			synchronized (this) {
				if (getModuleState() != ModuleState.NEW) {
					return;
				}
				setModuleState(ModuleState.INITIALIZING);
				LOG.info("InputWSService initializing");
			}

			setModuleState(ModuleState.RECOVERY);
			recovery();
			Endpoint.publish(Engine.INPUTWS_ENDPOINT_URL, new InputWS());
			setModuleState(ModuleState.RUNNING);
			LOG.info("InputWSService running");
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
			String message = String.format("InputWSService crashed - %s", e.getMessage());
			LOG.fatal(message);
		}
	}

	private void recovery() throws Exception {
		ImportingInputGraphStates importedInputGraphStates = new ImportingInputGraphStates();
		Collection<String> importingGraphUuids = importedInputGraphStates.getAllImportingGraphUuids();
		if (importingGraphUuids != null && !importingGraphUuids.isEmpty()) {
			LOG.info("InputWSService starts recovery");
			for (String uuid : importingGraphUuids) {
				File inputFile = new File(Engine.INPUTWS_DIR + uuid + ".dat");
				inputFile.delete();
			}
			importedInputGraphStates.deleteAllImportingGraphUuids();
			LOG.info("InputWSService ends recovery");
		}
	}
}
