package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashSet;

import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.rdf.model.Model;

import cz.cuni.mff.odcleanstore.configuration.EngineConfig;
import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.db.VirtuosoJdbc4ConnectionForRdf;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.Metadata;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;

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
		try {
			deleteGraphsInCleanDB();

			Collection<String> graphs = getAllGraphNames();
			JDBCConnectionCredentials creditDirty = ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials();
			JDBCConnectionCredentials creditClean = ConfigLoader.getConfig().getEngineGroup().getCleanDBJDBCConnectionCredentials();
			
			for (String graphName : graphs) {
				Model srcModel = null;
				Model dstModel = null;
				try {
					srcModel = VirtModel.openDatabaseModel(graphName, creditDirty.getConnectionString(), creditDirty.getUsername(), creditDirty.getPassword());
					dstModel = VirtModel.openDatabaseModel(graphName, creditClean.getConnectionString(), creditClean.getUsername(), creditClean.getPassword());
					
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
		} catch(Exception e) { 
			throw new PipelineGraphManipulatorException(format(ERROR_REPLACE_GRAPHS_IN_CLEANDB), e);
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
		EngineConfig engineConfig = ConfigLoader.getConfig().getEngineGroup();
		String uuid = graphStatus.getUuid();
		
		HashSet<String> graphs = graphStatus.getAttachedGraphs();
		graphs.add(engineConfig.getDataGraphURIPrefix() + uuid);
		graphs.add(engineConfig.getMetadataGraphURIPrefix() + uuid);
		graphs.add(engineConfig.getProvenanceMetadataGraphURIPrefix() + uuid);
		
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
		
		EngineConfig engineConfig = ConfigLoader.getConfig().getEngineGroup();
		String dataGraphURI = engineConfig.getDataGraphURIPrefix() + uuid;
		String metadataGraphURI = engineConfig.getMetadataGraphURIPrefix() + uuid;
		String provenanceGraphURI = engineConfig.getProvenanceMetadataGraphURIPrefix() + uuid;
			
		VirtuosoJdbc4ConnectionForRdf con = null;
		try {
			con = VirtuosoJdbc4ConnectionForRdf.createDirtyDbConnection();
			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.metadataGraph + ">", "<" + metadataGraphURI + ">", "<" + metadataGraphURI + ">");
			
			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.insertedAt + ">", inserted, "<" + metadataGraphURI + ">");
			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.insertedBy + ">", "'scraper'", "<" + metadataGraphURI + ">");
			for (String source : metadata.source) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.source + ">", "<" + source + ">", "<" + metadataGraphURI + ">");
			}
			for (String publishedBy : metadata.publishedBy) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.publishedBy + ">", "<" + publishedBy + ">", "<" + metadataGraphURI + ">");
			}
			if (metadata.license != null) {
				for (String license : metadata.license) {
					con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.license + ">", "<" + license + ">", "<" + metadataGraphURI + ">");
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
			EngineConfig engineConfig = ConfigLoader.getConfig().getEngineGroup();
			JDBCConnectionCredentials creditClean = engineConfig.getCleanDBJDBCConnectionCredentials();
			JDBCConnectionCredentials creditDirty = engineConfig.getDirtyDBJDBCConnectionCredentials();
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
	
	private String format(String message) {
		return FormatHelper.formatGraphMessage(message, graphStatus.getUuid());
	}
}
