/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import javax.xml.ws.Endpoint;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public final class ScraperService extends Service implements Runnable {

	// private ImportingInputGraphStates _importedInputGraphStates;

	public ScraperService(Engine engine) {
		super(engine);
	}

	@Override
	public final void run() {
		try {
			if (getModuleState() != ModuleState.NEW) {
				return;
			}
			setModuleState(ModuleState.INITIALIZING);
			// _importedInputGraphStates = new ImportingInputGraphStates();
			Endpoint.publish(Engine.SCRAPER_ENDPOINT_URL, new Scraper());
			setModuleState(ModuleState.RUNNING);
		} catch (Exception e) {
			setModuleState(ModuleState.CRASHED);
		}
	}
}
