package cz.cuni.mff.odcleanstore.engine.inputws;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.comlib.ComlibUtils;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.EngineException;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.model.Credentials;
import cz.cuni.mff.odcleanstore.engine.db.model.DbOdcsContext;
import cz.cuni.mff.odcleanstore.engine.db.model.DbOdcsException;
import cz.cuni.mff.odcleanstore.shared.FileUtils;
import cz.cuni.mff.odcleanstore.shared.Utils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;

public class InsertExecutor extends SoapInsertMethodExecutor {
	
    private static final Logger LOG = LoggerFactory.getLogger(InsertExecutor.class);

	private static InputGraphStatus importedInputGraphStates = new InputGraphStatus();

	private String user;
	private String pipelineName;
	private UUID uuid;
	private BufferedWriter metadataWriter, provenanceWriter, payloadWriter;

	private String inputDirectory;
	private String dataGraphURI;
	private boolean isActive;

	public InsertExecutor() throws InsertExecutorException {
		try {
			inputDirectory = Engine.getCurrent().getDirtyDBImportExportDir();
		} catch (EngineException e) {
			throw new InsertExecutorException(e);
		}
	}

	@Override
	protected void onElement(String name, String content) throws InsertExecutorException {

		if (name.equals("payload")) {
			writePayload(content);
		} else if (name.equals("provenance")) {
			writeProvenance(content);
		} else if (name.equals("publishedBy")) {
			writeMetadata(ODCS.publishedBy, "<" + content + ">");
		} else if (name.equals("source")) {
			writeMetadata(ODCS.source, "<" + content + ">");
		} else if (name.equals("license")) {
			writeMetadata(ODCS.license, "<" + content + ">");
		} else if (name.equals("dataBaseUrl")) {
			writeMetadata(ODCS.dataBaseUrl, "<" + content + ">");
		} else if (name.equals("updateTag")) {
			writeMetadata(ODCS.updateTag, "'" + Utils.escapeSPARQLLiteral(content) + "'");
		} else if (name.equals("user")) {
			user(content);
		} else if (name.equals("password")) {
			password(content);
		} else if (name.equals("pipelineName")) {
			pipelineName(content);
		} else if (name.equals("uuid")) {
			uuid(content);
		}
	}

	private void user(String content) {
		LOG.info("InputWS - Incoming request execution started", content);
		user = content;
	}

	private void password(String content) throws InsertExecutorException {

		DbOdcsContext context = null;
		Credentials credential = null;
		try {
			context = new DbOdcsContext();
			credential = context.selectScraperCredentials(user);
		} catch (DbOdcsException e) {
			throw new InsertExecutorException(e);
		} finally {
			if (context != null) {
				context.closeQuietly();
			}
		}

		if (credential == null) {
			String message = String.format("Bad credentials for user %s", user != null ? user : "");
			throw new InsertExecutorException(InputWSErrorEnumeration.NOT_AUTHORIZED, message);
		}

		try {
			if (!credential.passwordHash.equals(calculateHash(content, credential.salt))) {
				String message = String.format("Bad credentials for user %s", user != null ? user : "");
				throw new InsertExecutorException(InputWSErrorEnumeration.NOT_AUTHORIZED, message);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new InsertExecutorException(e);
		}
	}

	private void pipelineName(String content) {
		pipelineName = content;
	}

	private void uuid(String content) throws InsertExecutorException {
		if (content.isEmpty()) {
			throw new InsertExecutorException(InputWSErrorEnumeration.UUID_BAD_FORMAT, "uuid is empty");
		}

		try {
			uuid = UUID.fromString(content);
		} catch (IllegalArgumentException e) {
			throw new InsertExecutorException(InputWSErrorEnumeration.UUID_BAD_FORMAT, "Bad uuid format");
		}

		try {
			importedInputGraphStates.beginImport(uuid.toString(), pipelineName);
		} catch (InputGraphStatusException e) {
			if (e.getId() == InputWSErrorEnumeration.FATAL_ERROR) {
				throw new InsertExecutorException(e);
			} else {
				throw new InsertExecutorException(e.getId(), e.getMessage());
			}
		}
		
		LOG.info("InputWS - Incoming request for graph {} accepted", uuid);
		isActive = true;
		dataGraphURI = ODCSInternal.dataGraphUriPrefix + uuid;
		writeMetadataFirst();
	}

	private void writeMetadataFirst() throws InsertExecutorException {
		metadataWriter = createFile(inputDirectory + uuid + "-m.ttl");

		writeMetadata(ODCS.metadataGraph, "<" + ODCSInternal.metadataGraphUriPrefix + uuid + ">");
		writeMetadata(ODCS.insertedAt, FormatHelper.getTypedW3CDTFCurrent());
		writeMetadata(ODCS.insertedBy, "'" + Utils.escapeSPARQLLiteral(user) + "'");
	}

	private void writeMetadata(String predicate, String object) throws InsertExecutorException {
		try {
			metadataWriter.append("<");
			metadataWriter.append(dataGraphURI);
			metadataWriter.append("> <");
			metadataWriter.append(predicate);
			metadataWriter.append("> ");
			metadataWriter.append(object);
			metadataWriter.append(" .\r\n");
		} catch (Exception e) {
			throw new InsertExecutorException(e);
		}
	}

	private void writeProvenance(String content) throws InsertExecutorException {
		if (provenanceWriter == null) {
			content = FileUtils.removeInitialBOMXml(content);
			String fileName = inputDirectory + uuid + (content.startsWith("<?xml") ? "-pvm.rdf" : "-pvm.ttl");
			provenanceWriter = createFile(fileName);
		}
		try {
			provenanceWriter.write(content);
		} catch (IOException e) {
			throw new InsertExecutorException(e);
		}
	}

	private void writePayload(String content) throws InsertExecutorException {
		if (payloadWriter == null) {
			content = FileUtils.removeInitialBOMXml(content);
			String fileName = inputDirectory + uuid + (content.startsWith("<?xml") ? "-d.rdf" : "-d.ttl");
			payloadWriter = createFile(fileName);
		}
		try {
			payloadWriter.write(content);
		} catch (IOException e) {
			throw new InsertExecutorException(e);
		}
	}

	private BufferedWriter createFile(String name) throws InsertExecutorException {
		try {
			FileOutputStream fos = new FileOutputStream(name);
			return new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new InsertExecutorException(e);
		} catch (FileNotFoundException e) {
			throw new InsertExecutorException(e);
		}
	}

	@Override
	public void endParsing() throws InsertExecutorException {
		ComlibUtils.closeQuietly(metadataWriter);
		ComlibUtils.closeQuietly(provenanceWriter);
		ComlibUtils.closeQuietly(payloadWriter);

		try {
			if (uuid != null) {
				importedInputGraphStates.commitImport(uuid.toString());
				Engine.getCurrent().signalToPipelineService();
				LOG.info("InputWS - Incoming request for graph {} successfully ended", uuid);
				LOG.info("InputWS - Graph {} queued for pipeline", uuid);
			}
		} catch (Exception e) {
			throw new InsertExecutorException(e);
		}
	}

	/**
	 * Calculates an MD5 hash of the given string value.
	 * 
	 * @param pattern
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	static String calculateHash(String password, String salt) throws NoSuchAlgorithmException {
		String pattern = password + salt;

		MessageDigest algorithm = MessageDigest.getInstance("MD5");

		algorithm.reset();
		algorithm.update(pattern.getBytes());

		byte[] hash = algorithm.digest();

		String result = "";
		for (int i = 0; i < hash.length; i++) {
			String tmp = (Integer.toHexString(0xFF & hash[i]));
			if (tmp.length() == 1)
				result += "0" + tmp;
			else
				result += tmp;
		}

		return result;
	}

	void cleanOnError() {
		if (!isActive) {
			return;
		}
		String uuidStr = uuid.toString();
		deleteInputFiles(uuidStr);
		revertImport(uuidStr);
	}

	static void recoveryOnStartup() {
		String[] uuids = null;
		while (true) {
			try {
				LOG.info("InputWS - Starting recovery on startup");
				uuids = importedInputGraphStates.getAllImportingGraphUuids();
				break;
			} catch (InputGraphStatusException e) {
				LOG.warn("Input WS - get all importing graphs failure");
				try {
					Thread.sleep(3000);
				} catch (Exception ee) {
					// do nothing
				}
			}
		}

		if (uuids != null) {
			for (String uuid : uuids) {
				LOG.info("InputWS - Recovery previously crashed importing graph {}", uuid);
				deleteInputFiles(uuid);
				revertImport(uuid);
			}
		}
		LOG.info("InputWS - Recovery on startup successfully ended");
	}

	static synchronized void revertImport(String uuid) {
		while (true) {
			try {
				importedInputGraphStates.revertImport(uuid);
				return;
			} catch (Exception e) {
				LOG.warn("Input WS - reverting status of bad import graph {} failure", uuid);
				try {
					Thread.sleep(3000);
				} catch (Exception ee) {
					// do nothing
				}
			}
		}
	}

	static boolean deleteInputFiles(String uuid) {
		boolean retVal = true;
		try {
			String inputDirPath = Engine.getCurrent().getDirtyDBImportExportDir();
			retVal &= deleteFile(new File(inputDirPath, uuid + "-d.rdf"));
			retVal &= deleteFile(new File(inputDirPath, uuid + "-d.ttl"));
			retVal &= deleteFile(new File(inputDirPath, uuid + "-m.rdf"));
			retVal &= deleteFile(new File(inputDirPath, uuid + "-m.ttl"));
			retVal &= deleteFile(new File(inputDirPath, uuid + "-pvm.rdf"));
			retVal &= deleteFile(new File(inputDirPath, uuid + "-pvm.ttl"));
			return retVal;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean deleteFile(File file) {
		try {
			if (!file.delete() && file.exists()) {
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
