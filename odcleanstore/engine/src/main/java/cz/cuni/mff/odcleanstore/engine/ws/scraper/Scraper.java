/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.ws.scraper;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.UUID;

import javax.jws.WebParam;
import javax.jws.WebService;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.IScraper;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.InsertException;
import cz.cuni.mff.odcleanstore.engine.ws.scraper.ifaces.Metadata;

/**
 * 
 * @author Petr Jerman <petr.jerman@centrum.cz>
 */
@WebService
public class Scraper implements IScraper {

	private static ImportingInputGraphStates _importedInputGraphStates = new ImportingInputGraphStates();

	@Override
	public void insert(@WebParam(name = "user") String user, @WebParam(name = "password") String password, @WebParam(name = "metadata") Metadata metadata,
			@WebParam(name = "rdfXmlPayload") String rdfXmlPayload) throws InsertException {

		try {
			if (user == null || password == null || !user.equals("scraper") || !password.equals("reparcs")) {
				throw InsertException.BAD_CREDENTIALS;
			}

			if (metadata == null) {
				throw new InsertException("metadata is null");
			}

			checkUuid(metadata.uuid);
			checkUries(metadata.publishedBy, 1,"publishedBy");
			checkUries(metadata.source, 1, "source");
			checkUries(metadata.license, 0, "license");
			checkUri(metadata.dataBaseUrl, "dataBaseUrl");
			checkUri(metadata.provenanceBaseUrl, "provenanceBaseUrl");

			if (rdfXmlPayload == null) {
				throw new InsertException("rdfXmlPayload is null");
			}

			String sessionUuid = _importedInputGraphStates.beginImportSession(metadata.uuid, null);
			saveFiles(metadata, rdfXmlPayload);
			_importedInputGraphStates.commitImportSession(sessionUuid);
			Engine.signalToPipelineService();

		} catch (InsertException e) {
			throw e;
		} catch (ImportingInputGraphStates.ServiceBusyException e) {
			throw InsertException.SERVICE_BUSY;
		} catch (ImportingInputGraphStates.DuplicatedUuid e) {
			throw InsertException.DUPLICATED_UUID;
		} catch (Exception e) {
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

	private void saveFiles(Metadata metadata, String rdfXmlPayload) throws Exception {
		FileOutputStream fout = null;
		ObjectOutputStream oos = null;
		try {
			fout = new FileOutputStream(Engine.SCRAPER_INPUT_DIR + metadata.uuid + ".dat");
			oos = new ObjectOutputStream(fout);
			oos.writeObject(FormatHelper.getW3CDTFCurrent());
			oos.writeObject(metadata);
			oos.writeObject(rdfXmlPayload);
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