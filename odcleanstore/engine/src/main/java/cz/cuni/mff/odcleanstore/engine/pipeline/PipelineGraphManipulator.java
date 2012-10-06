package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.EngineConfig;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.engine.inputws.ifaces.Metadata;
import cz.cuni.mff.odcleanstore.shared.Utils;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
/**
 *  @author Petr Jerman
 */
final class PipelineGraphManipulator {
	
	private static final String ERROR_DELETE_INPUT_FILE = "deleting input file.";
	private static final String ERROR_INPUT_FILE_STILL_EXIST = ERROR_DELETE_INPUT_FILE + ", file still exists.";
	private static final String ERROR_DELETE_GRAPHS_FROM_DIRTYDB = "deleting graphs from dirty db";
	private static final String ERROR_DELETE_GRAPHS_FROM_CLEANDB = "deleting graphs from clean db";
	// private static final String ERROR_DELETE_TEMP_GRAPHS_FROM_CLEANDB = "deleting temporary graphs from clean db";
	private static final String ERROR_REPLACE_GRAPHS_IN_CLEANDB = "replacing graphs in clean db from dirty db";
	private static final String ERROR_LOAD_GRAPHS_FROM_FILE = "loading graphs into clean db from input file";
	private static final String ERROR_LOAD_GRAPHS_FROM_CLEAN_DB = "loading graphs into dirty db from clean db";
	
	private PipelineGraphStatus graphStatus;
	
	PipelineGraphManipulator(PipelineGraphStatus graphStatus) {
		this.graphStatus = graphStatus;
	}
	
	void deleteInputFile() throws PipelineGraphManipulatorException {
		try {
			String inputDirPath = Engine.getCurrent().getDirtyDBImportExportDir();
			File inputFile = null;
			
			inputFile = new File(inputDirPath  + graphStatus.getUuid() + ".hdr");
			if (!inputFile.delete() && inputFile.exists()) {
				throw new PipelineGraphManipulatorException(format(ERROR_INPUT_FILE_STILL_EXIST));
			}
			inputFile = new File(inputDirPath + graphStatus.getUuid() + ".rdf");
			if (!inputFile.delete() && inputFile.exists()) {
				throw new PipelineGraphManipulatorException(format(ERROR_INPUT_FILE_STILL_EXIST));
			}
			inputFile = new File(inputDirPath + graphStatus.getUuid() + ".ttl");
			if (!inputFile.delete() && inputFile.exists()) {
				throw new PipelineGraphManipulatorException(format(ERROR_INPUT_FILE_STILL_EXIST));
			}
			inputFile = new File(inputDirPath + graphStatus.getUuid() + "-pvm.rdf");
			if (!inputFile.delete() && inputFile.exists()) {
				throw new PipelineGraphManipulatorException(format(ERROR_INPUT_FILE_STILL_EXIST));
			}
			inputFile = new File(inputDirPath + graphStatus.getUuid() + "-pvm.ttl");
			if (!inputFile.delete() && inputFile.exists()) {
				throw new PipelineGraphManipulatorException(format(ERROR_INPUT_FILE_STILL_EXIST));
			}
		}
		catch(PipelineGraphManipulatorException e) { throw e; }
		catch(Exception e) {
			throw new PipelineGraphManipulatorException(format(ERROR_DELETE_INPUT_FILE), e);
		}
	}
	
	void clearGraphsInDirtyDB() throws PipelineGraphManipulatorException {
		try {
			clearGraphsFromDB(false, false);
		} catch(Exception e) {
			throw new PipelineGraphManipulatorException(format(ERROR_DELETE_GRAPHS_FROM_DIRTYDB), e);
		}
	}
	
	void clearGraphsInCleanDB() throws PipelineGraphManipulatorException {
		try {
			clearGraphsFromDB(true, true);
			clearGraphsFromDB(true, false);
		} catch(Exception e) {
			throw new PipelineGraphManipulatorException(format(ERROR_DELETE_GRAPHS_FROM_CLEANDB), e);
		}
	}
	
	// TODO must be refactored!
	void replaceGraphsInCleanDBFromDirtyDB() throws PipelineGraphManipulatorException {
		//		// delete temporary graphs in clean DB
		//		try {
		//		deleteGraphsFromDB(true, true);
		//		} catch(Exception e) {
		//		throw new PipelineGraphManipulatorException(format(ERROR_DELETE_TEMP_GRAPHS_FROM_CLEANDB), e);
		//		}
			
		// copy graphs from dirty to clean DB
		try {
			String[] graphs = getAllGraphNames();
			JDBCConnectionCredentials creditDirty = ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials();
			JDBCConnectionCredentials creditClean = ConfigLoader.getConfig().getEngineGroup().getCleanDBJDBCConnectionCredentials();
				
			for (String graphName : graphs) {
					
				File srcFile = null;
				File dstFile = null;
				OutputStreamWriter out = null;
				VirtuosoConnectionWrapper dirtyConnection = null;
				VirtuosoConnectionWrapper cleanConnection = null;
				try {
					String tempFileName = Utils.extractUUID(graphName) + "-temp.ttl";
					srcFile = new File(Engine.getCurrent().getDirtyDBImportExportDir() + tempFileName);
					dstFile = new File(Engine.getCurrent().getCleanDBImportExportDir() + tempFileName);
					srcFile.delete();
					dstFile.delete();
						
					// String destGraph = ODCS.engineTemporaryGraph + "/" + graphName;

					dirtyConnection = VirtuosoConnectionWrapper.createConnection(creditDirty);
					dirtyConnection.setQueryTimeout(0);
					String query = "CALL dump_graph_ttl('" + graphName + "', '" + srcFile.getAbsolutePath().replace("\\", "/") + "')";
					dirtyConnection.execute(query);
					    
					// move file if neccessary
					if(!srcFile.getCanonicalFile().equals(dstFile.getCanonicalFile())) {
						srcFile.renameTo(dstFile);
					}
					cleanConnection = VirtuosoConnectionWrapper.createConnection(creditClean);
					cleanConnection.setQueryTimeout(0);
					cleanConnection.clearGraph(graphName);
					query = "DB.DBA.TTLP (file_to_string_output ('" + dstFile.getAbsolutePath().replace("\\", "/") + "'), '" + graphName + "', '" + graphName + "',0)";
				    cleanConnection.execute(query);
				} finally {
					if (srcFile != null) {
						srcFile.delete();
					}
					if (dstFile != null) {
						dstFile.delete();
					}
				    if (out != null) {
				        out.close();
				    }
				    if (dirtyConnection != null) {
				    	dirtyConnection.closeQuietly();
				    }
				    if (cleanConnection != null) {
				    	cleanConnection.closeQuietly();
				    }
				}
			}

			// transactional processing - delete graph and replace it with temporary graphs in clean DB
			// VirtuosoJdbcConnectionForRdf con = VirtuosoJdbcConnectionForRdf.createCleanDbConnection();
			// for (String graphName : graphs) {
				// con.renameGraph(ODCS.engineTemporaryGraph + "/" + graphName, graphName);
				// con.commit();
			// }
			
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
	
	private String[] getAllGraphNames() {
		EngineConfig engineConfig = ConfigLoader.getConfig().getEngineGroup();
		String uuid = graphStatus.getUuid();
		
		ArrayList<String> graphs = new ArrayList<String>();
		graphs.add(engineConfig.getDataGraphURIPrefix() + uuid);
		graphs.add(engineConfig.getMetadataGraphURIPrefix() + uuid);
		graphs.add(engineConfig.getProvenanceMetadataGraphURIPrefix() + uuid);
		graphs.addAll(graphStatus.getAttachedGraphs());
		return graphs.toArray(new String[0]);
	}
	
	private void clearGraphsFromDB(boolean fromCleanDB, boolean temporaryGraphs) throws Exception  {
		VirtuosoConnectionWrapper con = null;
		try {
			String[] graphs = getAllGraphNames();
			con = fromCleanDB ? 
					VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getEngineGroup().getCleanDBJDBCConnectionCredentials()):
					VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials());
			con.setQueryTimeout(0);					
			for (String graphName : graphs) {
				if (temporaryGraphs) {
					graphName = ODCS.engineTemporaryGraph  + "/" + graphName; 
				}
				con.clearGraph(graphName);
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
		String uuid = graphStatus.getUuid();
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		String inputDirPath = Engine.getCurrent().getDirtyDBImportExportDir();
		boolean isPayloadRdfXml;
		boolean containProvenance;
		boolean isProvenanceRdfXml;
		try {
			String inputFileName = inputDirPath + uuid + ".hdr";
			fin = new FileInputStream(inputFileName);
			ois = new ObjectInputStream(fin);
			inserted = (String) ois.readObject();
			metadata = (Metadata) ois.readObject();
			isPayloadRdfXml = ois.readBoolean();
			containProvenance = ois.readBoolean();
			isProvenanceRdfXml = ois.readBoolean();
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
			
		VirtuosoConnectionWrapper con = null;
		try {
			con = VirtuosoConnectionWrapper.createConnection(ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials());
			con.setQueryTimeout(0);
			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.metadataGraph + ">", "<" + metadataGraphURI + ">", metadataGraphURI);
			
			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.insertedAt + ">", inserted, metadataGraphURI);
			con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.insertedBy + ">", "'scraper'", metadataGraphURI);
			for (String source : metadata.source) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.source + ">", "<" + source + ">", metadataGraphURI);
			}
			for (String publishedBy : metadata.publishedBy) {
				con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.publishedBy + ">", "<" + publishedBy + ">", metadataGraphURI);
			}
			if (metadata.license != null) {
				for (String license : metadata.license) {
					con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.license + ">", "<" + license + ">", metadataGraphURI);
				}
			}
			if (containProvenance) {
				if (isProvenanceRdfXml) {
					con.insertRdfXmlFromFile(metadata.dataBaseUrl, inputDirPath + uuid + "-pvm.rdf", provenanceGraphURI);
				} else {
					con.insertTtlFromFile(metadata.dataBaseUrl, inputDirPath + uuid + "-pvm.ttl", provenanceGraphURI);
				}
				con.insertQuad("<" + dataGraphURI + ">", "<" + ODCS.provenanceMetadataGraph + ">", "<" + provenanceGraphURI + ">", metadataGraphURI);
			}
			if (isPayloadRdfXml) {
				con.insertRdfXmlFromFile(metadata.dataBaseUrl, inputDirPath + uuid + ".rdf", dataGraphURI);
			} else {
				con.insertTtlFromFile(metadata.dataBaseUrl, inputDirPath + uuid + ".ttl", dataGraphURI);
			}
			con.commit();
		} finally {
			if (con != null) {
				con.closeQuietly();
			}
		}
	}

	private void loadGraphsIntoDirtyDBFromCleanDB() throws Exception {
		try {
			String[] graphs = getAllGraphNames();
			JDBCConnectionCredentials creditDirty = ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials();
			JDBCConnectionCredentials creditClean = ConfigLoader.getConfig().getEngineGroup().getCleanDBJDBCConnectionCredentials();
				
			for (String graphName : graphs) {
					
				File srcFile = null;
				File dstFile = null;
				OutputStreamWriter out = null;
				VirtuosoConnectionWrapper srcConnection = null;
				VirtuosoConnectionWrapper dstConnection = null;
				try {
					String tempFileName = Utils.extractUUID(graphName) + "-temp.ttl";
					srcFile = new File(Engine.getCurrent().getCleanDBImportExportDir() + tempFileName);
					dstFile = new File(Engine.getCurrent().getDirtyDBImportExportDir() + tempFileName);
					srcFile.delete();
					dstFile.delete();
						
					srcConnection = VirtuosoConnectionWrapper.createConnection(creditClean);
					srcConnection.setQueryTimeout(0);
					String query = "CALL dump_graph_ttl('" + graphName + "', '" + srcFile.getAbsolutePath().replace("\\", "/") + "')";
					srcConnection.execute(query);
					    
					// move file if neccessary
					if(!srcFile.getCanonicalFile().equals(dstFile.getCanonicalFile())) {
						srcFile.renameTo(dstFile);
					}
					dstConnection = VirtuosoConnectionWrapper.createConnection(creditDirty);
					dstConnection.setQueryTimeout(0);
					dstConnection.clearGraph(graphName);
					query = "DB.DBA.TTLP (file_to_string_output ('" + dstFile.getAbsolutePath().replace("\\", "/") + "'), '" + graphName + "', '" + graphName + "',0)";
				    dstConnection.execute(query);
				} finally {
					if (srcConnection != null) {
				    	srcConnection.closeQuietly();
				    }
				    if (dstConnection != null) {
				    	dstConnection.closeQuietly();
				    }
					if (srcFile != null) {
						srcFile.delete();
					}
					if (dstFile != null) {
						dstFile.delete();
					}
				    if (out != null) {
				        out.close();
				    }
				}
			}
		} catch(Exception e) { 
			throw new PipelineGraphManipulatorException(format(ERROR_LOAD_GRAPHS_FROM_CLEAN_DB), e);
		}
	}
	
	private String format(String message) {
		try {
			return FormatHelper.formatGraphMessage(message, graphStatus.getUuid());
		} catch(Exception e) {
			return message;
		}
	}
}
