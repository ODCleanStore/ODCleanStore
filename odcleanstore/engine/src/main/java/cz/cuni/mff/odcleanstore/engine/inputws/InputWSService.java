package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.File;
import java.util.Collection;

import javax.xml.ws.Endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.Service;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.engine.common.Utils.DirectoryException;

/**
 *  @author Petr Jerman
 */
public final class InputWSService extends Service {

	private static final Logger LOG = LoggerFactory.getLogger(InputWSService.class);
	
	private Endpoint endpoint;

	public InputWSService(Engine engine) {
		super(engine, "InputWSService");
	}

	@Override
	protected void initialize() throws Exception {
		recovery();
		String inputDirectory =  ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
		try {
			Utils.satisfyDirectory(inputDirectory);
		} catch (DirectoryException e) {
			throw new InputWSException("Input directory checking error", e);
		}
		endpoint = Endpoint.publish(ConfigLoader.getConfig().getInputWSGroup().getEndpointURL().toString(), new InputWS());
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
	
	@Override
	public void shutdown() throws Exception {
		if (endpoint != null) {
			endpoint.stop();
		}
	}
}
