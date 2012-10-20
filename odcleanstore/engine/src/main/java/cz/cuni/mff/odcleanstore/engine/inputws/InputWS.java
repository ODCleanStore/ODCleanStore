/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.util.UUID;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.IInputWS;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.InsertException;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.Metadata;
import cz.cuni.mff.odcleanstore.shared.FileUtils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

/**
 *  @author Petr Jerman
 */
@WebService
public class InputWS implements IInputWS {

	private static final Logger LOG = LoggerFactory.getLogger(InputWS.class);

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
			
			metadata.provenance = FileUtils.removeInitialBOMXml(metadata.provenance);

			checkUuid(metadata.uuid);
			checkUries(metadata.publishedBy, 1, "publishedBy");
			checkUries(metadata.source, 1, "source");
			checkUries(metadata.license, 0, "license");
			checkUri(metadata.dataBaseUrl, "dataBaseUrl");

			if (payload == null) {
				throw new InsertException("payload is null");
			}
			
			payload = FileUtils.removeInitialBOMXml(payload);

			String sessionUuid = _importedInputGraphStates.beginImportSession(metadata.uuid, metadata.pipelineName, null);
			saveFiles(metadata, payload);
			_importedInputGraphStates.commitImportSession(sessionUuid);
			Engine.getCurrent().signalToPipelineService();
			LOG.info("InputWS webservice ends processing for input graph {}",metadata.uuid);

		} catch (InsertException e) {
			LOG.warn("InputWS webservice - insert exception {} : {}", e.getMessage(), e.getMoreInfo());
			throw e;
		} catch (InputGraphStatus.ServiceBusyException e) {
			LOG.warn("InputWS webservice - insert exception {} : {}", InsertException.SERVICE_BUSY.getMessage(), InsertException.SERVICE_BUSY.getMoreInfo());
			throw InsertException.SERVICE_BUSY;
		} catch (InputGraphStatus.DuplicatedUuid e) {
			LOG.warn("InputWS webservice - insert exception {} : {}", InsertException.DUPLICATED_UUID.getMessage(), InsertException.DUPLICATED_UUID.getMoreInfo());
			throw InsertException.DUPLICATED_UUID;
		} catch (InputGraphStatus.UnknownPipelineName e) {
			LOG.warn("InputWS webservice - insert exception {} : {}", InsertException.UNKNOWN_PIPELINENAME.getMessage(), InsertException.UNKNOWN_PIPELINENAME.getMoreInfo());
			throw InsertException.UNKNOWN_PIPELINENAME;
		} catch (InputGraphStatus.UnknownPipelineDefaultName e) {
			LOG.warn("InputWS webservice - insert exception {} : {}", InsertException.FATAL_ERROR.getMessage(), "Unknown pipeline default name");
			throw InsertException.FATAL_ERROR;
		} catch (Exception e) {
			LOG.warn("InputWS webservice - insert exception {} : {}", InsertException.FATAL_ERROR.getMessage(), InsertException.FATAL_ERROR.getMoreInfo());
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
		String inputDirectory =  Engine.getCurrent().getDirtyDBImportExportDir();
		
		String dataGraphURI = ODCSInternal.dataGraphUriPrefix + metadata.uuid;
		String metadataGraphURI = ODCSInternal.metadataGraphUriPrefix + metadata.uuid;
		
		StringBuilder metadatattl = new StringBuilder();
		
		append(metadatattl, dataGraphURI, ODCS.dataBaseUrl, "<" + metadata.dataBaseUrl + ">");
		append(metadatattl, dataGraphURI, ODCS.metadataGraph, "<" + metadataGraphURI + ">");	
		append(metadatattl, dataGraphURI, ODCS.insertedAt, FormatHelper.getTypedW3CDTFCurrent());
		append(metadatattl, dataGraphURI, ODCS.insertedBy, "'scraper'");
		append(metadatattl, dataGraphURI, ODCS.updateTag, "'" + metadata.updateTag + "'");
		
		for (String source : metadata.source) {
			append(metadatattl, dataGraphURI, ODCS.source, "<" + source + ">");
		}
		for (String publishedBy : metadata.publishedBy) {
			append(metadatattl, dataGraphURI, ODCS.publishedBy, "<" + publishedBy + ">");
		}
		if (metadata.license != null) {
			for (String license : metadata.license) {
				append(metadatattl, dataGraphURI, ODCS.license, "<" + license + ">");
			}
		}

		Writer output = null;
		try {
			String fullFileName = inputDirectory + metadata.uuid + "-m.ttl";
			// String metadatastr = Utils.unicodeToAscii(metadatattl.toString());
			// output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "US-ASCII"));
			output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "UTF-8"));
			output.write(metadatattl.toString());
		} finally {
			if (output != null) {
				output.close();
			}
		}
		
		output = null;
		boolean containProvenance = metadata.provenance != null && !metadata.provenance.isEmpty();
		if (containProvenance) {
			try {
				boolean isProvenanceRdfXml = containProvenance && metadata.provenance.startsWith("<?xml");
				String fullFileName = inputDirectory + metadata.uuid + (isProvenanceRdfXml ? "-pvm.rdf" : "-pvm.ttl");
				if(!isProvenanceRdfXml) {
					// metadata.provenance = Utils.unicodeToAscii(metadata.provenance);
					// output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "US-ASCII"));
					output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "UTF-8"));
				} else {
					output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "UTF-8"));
				}
				output.write(metadata.provenance);
			} finally {
				if (output != null) {
					output.close();
				}
			}
		}
		
		output = null;
		try {
			boolean isPayloadRdfXml = payload.startsWith("<?xml");
			String fullFileName = inputDirectory + metadata.uuid + (isPayloadRdfXml ? "-d.rdf" : "-d.ttl");
			if (!isPayloadRdfXml) {
				// payload = Utils.unicodeToAscii(payload);
				// output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "US-ASCII"));
				output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "UTF-8"));
			} else {
				output = new BufferedWriter(new OutputStreamWriter( new FileOutputStream(fullFileName), "UTF-8"));
			}
			output.write(payload);
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}
	
	private void append(StringBuilder metadata, String subject, String predicate, String object) {
		metadata.append("<");
		metadata.append(subject);
		metadata.append("> <");	
		metadata.append(predicate);
		metadata.append("> ");
		metadata.append(object);
		metadata.append(" .\r\n");
	}
}