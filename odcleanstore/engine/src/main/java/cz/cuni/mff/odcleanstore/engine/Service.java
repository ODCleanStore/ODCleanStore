/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine;

import cz.cuni.mff.odcleanstore.engine.common.Module;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;

import cz.cuni.mff.odcleanstore.engine.pipeline.PipelineService;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ScraperService;
import cz.cuni.mff.odcleanstore.engine.ws.user.UserService;

/**
 * @author jermanp
 * 
 */
public abstract class Service extends Module {

	protected Service(Engine engine) {
		if (engine == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected final void setModuleState(ModuleState _moduleState) {
		super.setModuleState(_moduleState);
		Engine.onServiceStateChanged(this);
	}

	protected static final PipelineService getPipelineService() {
		return Engine.getPipelineService();
	}

	protected static final ScraperService getScraperService() {
		return Engine.getScraperService();
	}

	protected static final UserService getUserService() {
		return Engine.getUserService();
	}
}
