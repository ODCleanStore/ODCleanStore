package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.File;
import java.util.Collection;

import javax.xml.ws.Endpoint;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.ModuleState;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.engine.common.Utils.DirectoryException;

/**
 *  @author Petr Jerman
 */
public final class InputWSService extends Service implements Runnable {

	private static final Logger LOG = Logger.getLogger(InputWSService.class);
	
	private Endpoint _endpoint;

	public InputWSService(Engine engine) {
		super(engine);
	}
	
	@Override
	public void shutdown() {
		if(_endpoint != null) {
			_endpoint.stop();
		}
	}

	@Override
	public void run() {
		try {
			synchronized (this) {
				if (getModuleState() != ModuleState.NEW) {
					return;
				}
				LOG.info("InputWSService initializing");
				setModuleState(ModuleState.INITIALIZING);
			}
			initialize();
			setModuleState(ModuleState.RECOVERY);
			recovery();
			_endpoint = Endpoint.publish(ConfigLoader.getConfig().getInputWSGroup().getEndpointURL().toString(), new InputWS());
			LOG.info("InputWSService running");
			setModuleState(ModuleState.RUNNING);
		} catch (Exception e) {
			String message = String.format("InputWSService crashed - %s", e.getMessage());
			LOG.fatal(message);
			setModuleState(ModuleState.CRASHED);
		}
	}
	
	private void initialize() throws Exception {
		String inputDirectory =  ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
		try {
			Utils.satisfyDirectory(inputDirectory);
		} catch (DirectoryException e) {
			throw new InputWSException("Input directory checking error", e);
		}
	}

	private void recovery() throws Exception {
		String inputDirectory =  ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
		
		InputGraphStatus importedInputGraphStates = new InputGraphStatus();
		Collection<String> importingGraphUuids = importedInputGraphStates.getAllImportingGraphUuids();
		if (importingGraphUuids != null && !importingGraphUuids.isEmpty()) {
			LOG.info("InputWSService starts recovery");
			for (String uuid : importingGraphUuids) {
				File inputFile = new File(inputDirectory + uuid + ".dat");
				inputFile.delete();
			}
			importedInputGraphStates.deleteAllImportingGraphUuids();
			LOG.info("InputWSService ends recovery");
		}
	}
}
