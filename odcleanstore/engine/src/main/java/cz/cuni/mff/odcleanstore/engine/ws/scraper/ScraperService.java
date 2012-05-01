/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import java.io.File;
import java.util.Collection;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public final class ScraperService extends Service implements Runnable {

	private static final Logger LOG = Logger.getLogger(ScraperService.class);

	public ScraperService(Engine engine) {
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
				LOG.info("ScraperService initializing");
			}

			setModuleState(ModuleState.RECOVERY);
			recovery();
			Endpoint.publish(Engine.SCRAPER_ENDPOINT_URL, new Scraper());
			setModuleState(ModuleState.RUNNING);
			LOG.info("ScraperService running");
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
			String message = String.format("ScraperService crashed - %s", e.getMessage());
			LOG.fatal(message);
		}
	}

	private void recovery() throws Exception {
		ImportingInputGraphStates importedInputGraphStates = new ImportingInputGraphStates();
		Collection<String> importingGraphUuids = importedInputGraphStates.getAllImportingGraphUuids();
		if (importingGraphUuids != null && !importingGraphUuids.isEmpty()) {
			LOG.info("ScraperService starts recovery");
			for (String uuid : importingGraphUuids) {
				File inputFile = new File(Engine.SCRAPER_INPUT_DIR + uuid + ".dat");
				inputFile.delete();
			}
			importedInputGraphStates.deleteAllImportingGraphUuids();
			LOG.info("ScraperService ends recovery");
		}
	}
}
