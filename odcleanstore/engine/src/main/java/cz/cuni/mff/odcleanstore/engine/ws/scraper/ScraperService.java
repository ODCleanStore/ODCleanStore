/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import java.io.File;
import java.util.Collection;

import javax.xml.ws.Endpoint;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public final class ScraperService extends Service implements Runnable {

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
			}

			setModuleState(ModuleState.RECOVERY);
			recovery();
			Endpoint.publish(Engine.SCRAPER_ENDPOINT_URL, new Scraper());
			setModuleState(ModuleState.RUNNING);
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
		}
	}

	private void recovery() throws Exception {
		ImportingInputGraphStates importedInputGraphStates = new ImportingInputGraphStates();
		Collection<String> importingGraphUuids = importedInputGraphStates.getAllImportingGraphUuids();
		for (String uuid : importingGraphUuids) {
			File inputFile = new File(Engine.SCRAPER_INPUT_DIR + uuid + ".dat");
			inputFile.delete();
		}
		importedInputGraphStates.deleteAllImportingGraphUuids();
	}
}
