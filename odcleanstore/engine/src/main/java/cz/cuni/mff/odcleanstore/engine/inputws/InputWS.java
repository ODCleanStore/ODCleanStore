/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.UUID;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.log4j.Logger;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.common.Utils;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.IInputWS;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.InsertException;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.Metadata;

/**
 *  @author Petr Jerman
 */
@WebService
public class InputWS implements IInputWS {

	private static final Logger LOG = Logger.getLogger(InputWS.class);

	private static InputGraphStatus _importedInputGraphStates = new InputGraphStatus();
	
	@Override
	public void insert(@WebParam(name = "user") String user, @WebParam(name = "password") String password, @WebParam(name = "metadata") Metadata metadata,
			@WebParam(name = "payload") String payload) throws InsertException {

		LOG.info("InputWS webservice starts processing for input");
		
		try {
			if (user == null || password == null || !user.equals("scraper") || !password.equals("reparcs")) {
				throw InsertException.BAD_CREDENTIALS;
			}

			if (metadata == null) {
				throw new InsertException("metadata is null");
			}
			
			metadata.provenance = Utils.removeInitialBOMXml(metadata.provenance);

			checkUuid(metadata.uuid);
			checkUries(metadata.publishedBy, 1, "publishedBy");
			checkUries(metadata.source, 1, "source");
			checkUries(metadata.license, 0, "license");
			checkUri(metadata.dataBaseUrl, "dataBaseUrl");

			if (payload == null) {
				throw new InsertException("payload is null");
			}
			
			payload = Utils.removeInitialBOMXml(payload);

			String sessionUuid = _importedInputGraphStates.beginImportSession(metadata.uuid, metadata.pipelineName, null);
			saveFiles(metadata, payload);
			_importedInputGraphStates.commitImportSession(sessionUuid);
			Engine.signalToPipelineService();
			LOG.info(String.format("InputWS webservice ends processing for input graph %s",metadata.uuid));

		} catch (InsertException e) {
			LOG.warn(String.format("InputWS webservice - insert exception %s : %s", e.getMessage(), e.getMoreInfo()));
			throw e;
		} catch (InputGraphStatus.ServiceBusyException e) {
			LOG.warn(String.format("InputWS webservice - insert exception %s : %s", InsertException.SERVICE_BUSY.getMessage(), InsertException.SERVICE_BUSY.getMoreInfo()));
			throw InsertException.SERVICE_BUSY;
		} catch (InputGraphStatus.DuplicatedUuid e) {
			LOG.warn(String.format("InputWS webservice - insert exception %s : %s", InsertException.DUPLICATED_UUID.getMessage(), InsertException.DUPLICATED_UUID.getMoreInfo()));
			throw InsertException.DUPLICATED_UUID;
		} catch (InputGraphStatus.UnknownPipelineName e) {
			LOG.warn(String.format("InputWS webservice - insert exception %s : %s", InsertException.UNKNOWN_PIPELINENAME.getMessage(), InsertException.UNKNOWN_PIPELINENAME.getMoreInfo()));
			throw InsertException.UNKNOWN_PIPELINENAME;
		} catch (InputGraphStatus.UnknownPipelineDefaultName e) {
			LOG.warn(String.format("InputWS webservice - insert exception %s : %s", InsertException.FATAL_ERROR.getMessage(), "Unknown pipeline default name"));
			throw InsertException.FATAL_ERROR;
		} catch (Exception e) {
			LOG.warn(String.format("InputWS webservice - insert exception %s : %s", InsertException.FATAL_ERROR.getMessage(), InsertException.FATAL_ERROR.getMoreInfo()));
			throw InsertException.FATAL_ERROR;
		}
	}

	private void checkUuid(String uuid) throws InsertException {
		if (uuid == null) {
			throw InsertException.UUID_BAD_FORMAT;
		}

		try {
			UUID.fromString(uuid);
		} catch (IllegalArgumentException e) {
			throw InsertException.UUID_BAD_FORMAT;
		}
	}
		
	private void checkUri(String uri, String moreInsertExceptionInfo) throws InsertException {
		if (uri == null || uri.isEmpty()) {
			throw new InsertException(moreInsertExceptionInfo);
		}
		try {
			URI.create(uri);
		} catch (Exception e) {
			throw new InsertException(moreInsertExceptionInfo);
		}
	}

	private void checkUries(String[] uries, int minCardinality, String moreInsertExceptionInfo) throws InsertException {
		int count = 0;

		if (uries == null) {
			if (minCardinality > 0) {
				throw new InsertException(String.format(" %s - cardinality must be %d minimal", moreInsertExceptionInfo, minCardinality));
			} else {
				return;
			}
		}

		for (String uri : uries) {
			checkUri(uri, moreInsertExceptionInfo);
			count++;
		}

		if (count < minCardinality) {
			throw new InsertException(String.format(" %s - cardinality must be %d minimal", moreInsertExceptionInfo, minCardinality));
		}
	}
	
	private void saveFiles(Metadata metadata, String payload) throws Exception {
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		String inputDirectory =  ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
		try {
			fout = new FileOutputStream(inputDirectory + metadata.uuid + ".dat");
			oos = new ObjectOutputStream(fout);
			oos.writeObject(FormatHelper.getTypedW3CDTFCurrent());
			oos.writeObject(metadata);
			oos.writeObject(payload);
		} finally {
			if (oos != null) {
				oos.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
	}
}