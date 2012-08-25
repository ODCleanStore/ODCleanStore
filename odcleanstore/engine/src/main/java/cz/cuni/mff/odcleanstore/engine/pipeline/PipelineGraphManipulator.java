package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;

import cz.cuni.mff.odcleanstore.configuration.BackendConfig;
import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.VirtuosoJdbc4ConnectionForRdf;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.Metadata;
import cz.cuni.mff.odcleanstore.vocabulary.DC;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

/**
 *  @author Petr Jerman
 */
final class PipelineGraphManipulator {
	
	private static final String ERROR_DELETE_INPUT_FILE = "deleting input file.";
	private static final String ERROR_INPUT_FILE_STILL_EXIST = ERROR_DELETE_INPUT_FILE + ", file still exists.";
	private static final String ERROR_DELETE_GRAPHS_FROM_DIRTYDB = "deleting graphs from dirty db";
	private static final String ERROR_DELETE_GRAPHS_FROM_CLEANDB = "deleting graphs from clean db";
	private static final String ERROR_REPLACE_GRAPHS_IN_CLEANDB = "replacing graphs in clean db from dirty db";
	private static final String ERROR_LOAD_GRAPHS_FROM_FILE = "loading graphs into clean db from input file";
	private static final String ERROR_LOAD_GRAPHS_FROM_CLEAN_DB = "loading graphs into dirty db from clean db";
	
	private PipelineGraphStatus graphStatus;
	
	PipelineGraphManipulator(PipelineGraphStatus graphStatus) {
		this.graphStatus = graphStatus;
	}
	
	void deleteInputFile() throws PipelineGraphManipulatorException {
		try {
			String inputDirPath = ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
			File inputFile = new File(inputDirPath + graphStatus.getUuid() + ".dat");
			
			if (!inputFile.delete() && inputFile.exists()) {
				throw new PipelineGraphManipulatorException(format(ERROR_INPUT_FILE_STILL_EXIST));
			}
		}
		catch(PipelineGraphManipulatorException e) { throw e; }
		catch(Exception e) {
			throw new PipelineGraphManipulatorException(format(ERROR_DELETE_INPUT_FILE), e);
		}
	}
	
	void deleteGraphsInDirtyDB() throws PipelineGraphManipulatorException {
		try {
			deleteGraphsFromDB(false);
		} catch(Exception e) {
			throw new PipelineGraphManipulatorException(format(ERROR_DELETE_GRAPHS_FROM_DIRTYDB), e);
			
		}
	}
	
	void deleteGraphsInCleanDB() throws PipelineGraphManipulatorException {
		try {
			deleteGraphsFromDB(true);
		} catch(Exception e) {
			throw new PipelineGraphManipulatorException(format(ERROR_DELETE_GRAPHS_FROM_CLEANDB), e);
		}
	}


	
	void replaceGraphsInCleanDBFromDirtyDB() throws PipelineGraphManipulatorException {
		VirtuosoJdbc4ConnectionForRdf con = null;
		try {
			Collection<String> graphs = getAllGraphNames();
			JDBCConnectionCredentials creditDirty = ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials();
			//JDBCConnectionCredentials creditClean = ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();
			con = VirtuosoJdbc4ConnectionForRdf.createCleanDbConnection();
			
			for (String graphName : graphs) {
				Model srcModel = null;
				// Model dstModel = null;
				con.clearGraph("<" + graphName + ">");
				try {
					srcModel = VirtModel.openDatabaseModel(graphName, creditDirty.getConnectionString(), creditDirty.getUsername(), creditDirty.getPassword());
					// dstModel = VirtModel.openDatabaseModel(graphName, creditClean.getConnectionString(), creditClean.getUsername(), creditClean.getPassword());
					Iterator<Triple> triples = srcModel.getGraph().find(null,null,null);
					while(triples.hasNext()) {
						Triple t = triples.next();
						con.insertQuad(nodeToString(t.getSubject()), nodeToString(t.getPredicate()), nodeToString(t.getObject()), "<" + graphName + ">");
					}
					// dstModel.add(srcModel);
					// dstModel.close();
					
				} finally {
					if (srcModel != null) {
						srcModel.close();
					}
					// if (dstModel != null) {
					//	dstModel.close();
					//}
				}
			}
			con.commit();
		} catch(Exception e) { 
			throw new PipelineGraphManipulatorException(format(ERROR_REPLACE_GRAPHS_IN_CLEANDB), e);
		}
		finally {
			if (con != null) {
				con.closeQuietly();
			}
		}
	}
	
	void loadGraphsIntoDirtyDB() throws PipelineGraphManipulatorException {
		String errorMessage = null;
		try {
			if (graphStatus.isInCleanDb()) {
				errorMessage = ERROR_LOAD_GRAPHS_FROM_CLEAN_DB;
				loadGraphsIntoDirtyDBFromCleanDB();
			}
			else {
				errorMessage = ERROR_LOAD_GRAPHS_FROM_FILE;
				loadGraphsIntoDirtyDBFromInputFile();
			}
		}
		catch(Exception e) {
			throw new PipelineGraphManipulatorException(format(errorMessage), e);
		}
	}
	
	private HashSet<String> getAllGraphNames() {
		BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();
		String uuid = graphStatus.getUuid();
		
		HashSet<String> graphs = graphStatus.getAttachedGraphs();
		graphs.add(backendConfig.getDataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getMetadataGraphURIPrefix() + uuid);
		graphs.add(backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid);
		
		return graphs;
	}
	
	private void deleteGraphsFromDB( boolean fromCleanDB) throws Exception  {
		Collection<String> graphs = getAllGraphNames();
		VirtuosoJdbc4ConnectionForRdf con = null;
		try {
			con = fromCleanDB ? 
					VirtuosoJdbc4ConnectionForRdf.createCleanDbConnection()
				 : VirtuosoJdbc4ConnectionForRdf.createDirtyDbConnection();
			for (String graphName : graphs) {
				con.clearGraph("<" + graphName + ">");
			}
			con.commit();
		} 
		finally {
			if (con != null) {
				con.closeQuietly();
			}
		}
	}
	
	private void loadGraphsIntoDirtyDBFromInputFile() throws Exception {
		String inserted = null;
		Metadata metadata = null;
		String payload = null;
		String uuid = graphStatus.getUuid();
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		String inputDirPath = ConfigLoader.getConfig().getInputWSGroup().getInputDirPath();
		try {
			String inputFileName = inputDirPath + uuid + ".dat";
			fin = new FileInputStream(inputFileName);
			ois = new ObjectInputStream(fin);
			inserted = (String) ois.readObject();
			metadata = (Metadata) ois.readObject();
			payload = (String) ois.readObject();
		} finally {
			if (ois != null) {
				ois.close();
			}
			if (fin != null) {
				fin.close();
			}
		}
		
		BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();
		String dataGraphURI = backendConfig.getDataGraphURIPrefix() + uuid;
		String metadataGraphURI = backendConfig.getMetadataGraphURIPrefix() + uuid;
		String provenanceGraphURI = backendConfig.getProvenanceMetadataGraphURIPrefix() + uuid;
			
		VirtuosoJdbc4ConnectionForRdf con = null;
		try {
			con = VirtuosoJdbc4ConnectionForRdf.createDirtyDbConnection();
			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.metadataGraph + ">", "<" + metadataGraphURI + ">", "<" + metadataGraphURI + ">");
			
			con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.insertedAt + ">", inserted, "<" + metadataGraphURI + ">");
			con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.insertedBy + ">", "'scraper'", "<" + metadataGraphURI + ">");
			for (String source : metadata.source) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.source + ">", "<" + source + ">", "<" + metadataGraphURI + ">");
			}
			for (String publishedBy : metadata.publishedBy) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + W3P.publishedBy + ">", "<" + publishedBy + ">", "<" + metadataGraphURI + ">");
			}
			if (metadata.license != null) {
				for (String license : metadata.license) {
					con.insertQuad("<" + dataGraphURI + ">", "<" + DC.license + ">", "<" + license + ">", "<" + metadataGraphURI + ">");
				}
			}
			if (metadata.provenance != null) {
				con.insertRdfXmlOrTtl(metadata.dataBaseUrl, metadata.provenance, provenanceGraphURI);
				con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.provenanceMetadataGraph + ">", "<" + provenanceGraphURI + ">", "<" + metadataGraphURI + ">");
			}
			con.insertRdfXmlOrTtl(metadata.dataBaseUrl, payload, dataGraphURI);
			con.commit();
		} finally {
			if (con != null) {
				con.closeQuietly();
			}
		}
	}

	private void loadGraphsIntoDirtyDBFromCleanDB() throws Exception {
			BackendConfig backendConfig = ConfigLoader.getConfig().getBackendGroup();
			JDBCConnectionCredentials creditClean = backendConfig.getCleanDBJDBCConnectionCredentials();
			JDBCConnectionCredentials creditDirty = backendConfig.getDirtyDBJDBCConnectionCredentials();
			Collection<String> graphs = getAllGraphNames();
		
			for (String graphName : graphs) {
				Model dstModel = null;
				Model srcModel = null;
				try {
					srcModel = VirtModel.openDatabaseModel(graphName, creditClean.getConnectionString(), creditClean.getUsername(), creditClean.getPassword());
					dstModel = VirtModel.openDatabaseModel(graphName, creditDirty.getConnectionString(), creditDirty.getUsername(), creditDirty.getPassword());
					dstModel.add(srcModel);
				} finally {
					if (srcModel != null) {
						srcModel.close();
					}
					if (dstModel != null) {
						dstModel.close();
					}
				}
			}
	}
	
	private String nodeToString(Node node) {
		if (node.isBlank()) {
			return "<_:" + node.getBlankNodeLabel() + ">";
		}
		else if( node.isURI()) {
			return "<" + node.getURI() + ">";
		}
		else if (node.isLiteral()) {
			String literal = "\"" + node.getLiteralLexicalForm() + "\"";
			String dataTypeURI = node.getLiteralDatatypeURI();
			if (dataTypeURI != null && dataTypeURI.length() > 0) {
				literal = literal + "^^<" + dataTypeURI + ">";
			}
			String language = node.getLiteralLanguage();
			if (language != null && language.length() > 0) {
				literal = literal + "@" + language;
			}
			return literal;
		}
		return node.toString();
	}
	
	private String format(String message) {
		return FormatHelper.formatGraphMessage(message, graphStatus.getUuid());
	}
}
