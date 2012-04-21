/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper;


import javax.xml.ws.Endpoint;

import cz.cuni.mff.odcleanstore.engine.Module;
import cz.cuni.mff.odcleanstore.engine.ModuleState;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
public class ScraperService extends Module {

	public ScraperService(Module parent) {
		super(parent);
	}

	@Override
	public void run() {
		try {
			if (get_moduleState() != ModuleState.NEW) {
				return;
			}			
			set_moduleState(ModuleState.INITIALIZING);
			Endpoint.publish("http://localhost:8088/odcleanstore/scraper", new Scraper());
			set_moduleState(ModuleState.RUNNING);
		} catch (Exception e) {
			set_moduleState(ModuleState.CRASHED);
		}
	}
}
