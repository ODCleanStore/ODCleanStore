package cz.cuni.mff.odcleanstore.engine.pipeline;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.engine.Engine;
import cz.cuni.mff.odcleanstore.engine.common.FormatHelper;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.ODCSInternal;
/**
 * RDF manipulator of current executing graph.
 *  @author Petr Jerman
 */
final class PipelineGraphManipulator {
    
    private static final String ERROR_DELETE_INPUT_FILES = "deleting input files";
    private static final String ERROR_DELETE_FILE = "deleting file";
    private static final String ERROR_FILE_STILL_EXIST = ERROR_DELETE_FILE + ", file %s still exists";
    private static final String ERROR_CLEAR_GRAPHS_IN_DIRTYDB = "clearing graphs in dirty db";
    private static final String ERROR_COPY_NEW_GRAPHS_TO_CLEANDB = "copying new graphs from dirty db to clean db";
    private static final String ERROR_RENAMING_OLD_GRAPHS_IN_CLEANDB = "renaming old graphs in clean db";
    private static final String ERROR_RENAMING_NEW_GRAPHS_IN_CLEANDB = "renaming new graphs in clean db";
    private static final String ERROR_CLEAR_OLD_GRAPHS_IN_CLEANDB = "clearing old graphs in clean db";
    private static final String ERROR_CLEAR_NEW_GRAPHS_IN_CLEANDB = "clearing new graphs in clean db";
    private static final String ERROR_CLEAR_GRAPHS_IN_CLEANDB = "clearing graphs in clean db";
    private static final String ERROR_LOAD_GRAPHS_FROM_FILE = "loading graphs into clean db from input files";
    private static final String ERROR_LOAD_METADATAGRAPH_DATABASE_URL = "loaded metadatagraph not contains databaseurl";
    private static final String ERROR_LOAD_GRAPHS_FROM_CLEAN_DB = "loading graphs into dirty db from clean db";
    private static final String ERROR_LOAD_METADATAGRAPH_FROM_FILE = 
            "loading metadatagraph (-m.ttl) into clean db from input file";
    
    private static final Logger LOG = LoggerFactory.getLogger(PipelineGraphManipulator.class);
    
    private PipelineGraphStatus graphStatus;
    
    /**
     * Create graph manipulator object for graph status object.
     * 
     * @param graphStatus graph status object
     */
    PipelineGraphManipulator(PipelineGraphStatus graphStatus) {
        this.graphStatus = graphStatus;
    }
    
    /**
     * Delete all input webservice files for current executing graph.
     */
    void deleteInputFiles() {
        try {
            String inputDirPath = Engine.getCurrent().getDirtyDBImportExportDir();

            safeDeleteFile(inputDirPath,  graphStatus.getUuid() + "-d.rdf");
            safeDeleteFile(inputDirPath,  graphStatus.getUuid() + "-d.ttl");
            safeDeleteFile(inputDirPath,  graphStatus.getUuid() + "-m.rdf");
            safeDeleteFile(inputDirPath,  graphStatus.getUuid() + "-m.ttl");
            safeDeleteFile(inputDirPath,  graphStatus.getUuid() + "-pvm.rdf");
            safeDeleteFile(inputDirPath,  graphStatus.getUuid() + "-pvm.ttl");
        } catch (Exception e) {
            LOG.error(format(ERROR_DELETE_INPUT_FILES));
        }
    }
    
    /**
     * Copying new graphs from dirty to clean database for current executing graph.
     * 
     * @throws PipelineGraphManipulatorException
     */
    void copyNewGraphsFromDirtyToCleanDB() throws PipelineGraphManipulatorException {
        try {
            String[] graphs = getAllGraphNames();
                
            for (int i = graphs.length - 1; i >= 0; i--) {
                VirtuosoConnectionWrapper dirtyConnection = null;
                VirtuosoConnectionWrapper cleanConnection = null;    
                OutputStreamWriter out = null;
                File srcFile = null;
                File dstFile = null;
                try {
                    String destGraph = ODCSInternal.newGraphPrefix + graphs[i];
                    String tempFileName = generateRandomFileNameForGraph();
                    srcFile = new File(Engine.getCurrent().getDirtyDBImportExportDir(), tempFileName);
                    dstFile = new File(Engine.getCurrent().getCleanDBImportExportDir(), tempFileName);

                    graphStatus.checkResetPipelineRequest();
                    dirtyConnection = createDirtyConnection();
                    LOG.info(String.format("Dumping data from dirty db to ttl temporary file for graph %s", graphs[i]));
                    dirtyConnection.exportToTTL(srcFile, graphs[i]);
                     
                    graphStatus.checkResetPipelineRequest();
                    // move file if neccessary
                    if (!srcFile.getCanonicalFile().equals(dstFile.getCanonicalFile())) {
                        srcFile.renameTo(dstFile);
                    }
                    
                    graphStatus.checkResetPipelineRequest();
                    cleanConnection = createCleanConnection();
                    LOG.info(String.format("Loading data from ttl temporary file to clean db for graph %s", graphs[i]));
                    cleanConnection.insertN3FromFile(dstFile, destGraph, graphs[i]);
                } finally {
                    if (dirtyConnection != null) {
                        dirtyConnection.closeQuietly();
                    }
                    if (cleanConnection != null) {
                        cleanConnection.closeQuietly();
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                            // do nothing
                        }
                    }
                    if (srcFile != null) {
                        safeDeleteFile(srcFile);
                    }
                    if (dstFile != null) {
                        safeDeleteFile(dstFile);
                    }
                }
            }
        } catch (Exception e) { 
            throw new PipelineGraphManipulatorException(format(ERROR_COPY_NEW_GRAPHS_TO_CLEANDB), e);
        }
    }
    
    /**
     * Add prefix for existing current executing graphs in clean database.
     * 
     * @throws PipelineGraphManipulatorException
     */
    void renameGraphsToOldGraphsInCleanDB() throws PipelineGraphManipulatorException {
        renameGraphsInCleanDB(null , ODCSInternal.oldGraphPrefix, ERROR_RENAMING_OLD_GRAPHS_IN_CLEANDB, false); 
    }
    
    /**
     * Remove prefix from new copied graphs in clean database.
     * 
     * @throws PipelineGraphManipulatorException
     */
    void renameNewGraphsToGraphsInCleanDB()  throws PipelineGraphManipulatorException {
        renameGraphsInCleanDB(ODCSInternal.newGraphPrefix,  null, ERROR_RENAMING_NEW_GRAPHS_IN_CLEANDB, true);
    }

    /**
     * Rename all executing graphs in clean database.
     * 
     * @param srcPrefix source prefix
     * @param dstPrefix destination prefix
     * @param errorMessage message for possilby exception
     * @param descendingDirection order of renaming
     * @throws PipelineGraphManipulatorException
     */
    private void renameGraphsInCleanDB(String srcPrefix, String dstPrefix, String errorMessage, boolean descendingDirection)
            throws PipelineGraphManipulatorException {

        try {
            String[] graphs = getAllGraphNames();
            if (descendingDirection) {
                for (int i = graphs.length - 1; i >= 0;  i--) { 
                    renameGraphInCleanDB(srcPrefix, dstPrefix, graphs[i]); 
                }
            } else {
                for (int i = 0; i < graphs.length;  i++) {
                    renameGraphInCleanDB(srcPrefix, dstPrefix, graphs[i]); 
                }
            }
        } catch (Exception e) { 
            throw new PipelineGraphManipulatorException(format(errorMessage), e);
        }
    }
    
    /**
     * Rename graph in clean db.
     * 
     * @param srcPrefix source prefix
     * @param dstPrefix destination prefix
     * @param graph graph name
     * @throws ConnectionException
     * @throws QueryException
     */
    private void renameGraphInCleanDB(String srcPrefix, String dstPrefix, String graph)
            throws ConnectionException, QueryException {

        VirtuosoConnectionWrapper cleanConnection = null;    
        try {
            cleanConnection = createCleanConnection();
            String srcGraph = (srcPrefix == null ? "" : srcPrefix) + graph;
            String dstGraph = (dstPrefix == null ? "" : dstPrefix) + graph;
            cleanConnection.renameGraph(srcGraph, dstGraph);
        } finally {
            if (cleanConnection != null) {
                cleanConnection.closeQuietly();
            }
        }
    }

    /**
     * Clear all executing graphs from dirty database.
     *  
     * @throws PipelineGraphManipulatorException
     */
    void clearGraphsInDirtyDB() throws PipelineGraphManipulatorException {
        try {
            String[] graphs = getAllGraphNames();
            for (String graphName : graphs) {
                VirtuosoConnectionWrapper dirtyConnection = null;    
                try {
                    dirtyConnection = createDirtyConnection();
                    dirtyConnection.dropGraph(graphName);
                } finally {
                    if (dirtyConnection != null) {
                        dirtyConnection.closeQuietly();
                    }
                }
            }
        } catch (Exception e) { 
            throw new PipelineGraphManipulatorException(format(ERROR_CLEAR_GRAPHS_IN_DIRTYDB), e);
        }
    }

    /**
     * Clear old version graphs from clean database for current executing graph.
     *  
     * @throws PipelineGraphManipulatorException
     */
    void clearOldGraphsInCleanDB() throws PipelineGraphManipulatorException {
        clearGraphsInCleanDB(ODCSInternal.oldGraphPrefix, ERROR_CLEAR_OLD_GRAPHS_IN_CLEANDB, false);
    }

    /**
     * Clear new copied graphs from clean database for current executing graph.
     *  
     * @throws PipelineGraphManipulatorException
     */
    void clearNewGraphsInCleanDB() throws PipelineGraphManipulatorException {
        clearGraphsInCleanDB(ODCSInternal.newGraphPrefix, ERROR_CLEAR_NEW_GRAPHS_IN_CLEANDB, false);
    }
    
    /**
     * Clear all existing graphs from clean database for current executing graph.
     *  
     * @throws PipelineGraphManipulatorException
     */
    void clearGraphsInCleanDB() throws PipelineGraphManipulatorException {
        clearGraphsInCleanDB(null, ERROR_CLEAR_GRAPHS_IN_CLEANDB, false);
    }
    

    /**
     * Clear all executing graphs from clean database with given prefixes.
     *  
     * @param prefix graph extra prefix
     * @param errorMessage message for possibly exception
     * @param descendingDirection clear direction
     * @throws PipelineGraphManipulatorException
     */
    private void clearGraphsInCleanDB(String prefix, String errorMessage, boolean descendingDirection)
            throws PipelineGraphManipulatorException {
        
        try {
            String[] graphs = getAllGraphNames();
            if (descendingDirection) {
                for (int i = graphs.length - 1; i >= 0;  i--) { 
                    clearGraphInCleanDB(prefix, graphs[i]); 
                }
            } else {
                for (int i = 0; i < graphs.length;  i++) {
                    clearGraphInCleanDB(prefix, graphs[i]); 
                }
            }
        } catch (Exception e) { 
            throw new PipelineGraphManipulatorException(format(errorMessage), e);
        }
    }
    
    /**
     * Delete graph in clean db.
     * 
     * @param prefix graph extra prefix
     * @param graph graph name 
     * @throws ConnectionException
     * @throws QueryException
     */
    private void clearGraphInCleanDB(String prefix, String graph)
            throws ConnectionException, QueryException {
        VirtuosoConnectionWrapper cleanConnection = null;    
        try {
            cleanConnection = createCleanConnection();
            String clearedGraph = (prefix == null ? "" : prefix) + graph;
            cleanConnection.dropGraph(clearedGraph);
        } finally {
            if (cleanConnection != null) {
                cleanConnection.closeQuietly();
            }
        }
    }
    
    /**
     * Loads graphs from input webservice generated files or clean database into dirty database.
     * 
     * @throws PipelineGraphManipulatorException
     */
    void loadGraphsIntoDirtyDB() throws PipelineGraphManipulatorException {
        String errorMessage = null;
        try {
            if (graphStatus.isInCleanDb()) {
                errorMessage = ERROR_LOAD_GRAPHS_FROM_CLEAN_DB;
                loadGraphsIntoDirtyDBFromCleanDB();
            } else {
                errorMessage = ERROR_LOAD_GRAPHS_FROM_FILE;
                loadGraphsIntoDirtyDBFromInputFile();
            }
            LOG.info(format("Data succefully loaded to dirty database"));
        } catch (Exception e) {
            throw new PipelineGraphManipulatorException(format(errorMessage), e);
        }
    }
    
    /**
     * Loads graphs from input webservice generated files into dirty database.
     * 
     * @throws Exception
     */
    private void loadGraphsIntoDirtyDBFromInputFile() throws Exception {
        String inputDirPath = Engine.getCurrent().getDirtyDBImportExportDir();
        String uuid = graphStatus.getUuid();
        
        String dataGraphURI = graphStatus.getNamedGraphsPrefix() + ODCSInternal.dataGraphUriInfix + uuid;
        String metadataGraphURI = graphStatus.getNamedGraphsPrefix() + ODCSInternal.metadataGraphUriInfix + uuid;
        String provenanceGraphURI = graphStatus.getNamedGraphsPrefix() + ODCSInternal.provenanceMetadataGraphUriInfix + uuid;
        
        String dataBaseUrl = null;
            
        VirtuosoConnectionWrapper con = null;
        try {
            con = createDirtyConnection();

            graphStatus.checkResetPipelineRequest();
            try {
                LOG.info(format("Loading metadata from ttl input file"));
                File mTTLFile = new File(inputDirPath, uuid + "-m.ttl"); 
                con.insertN3FromFile(mTTLFile, metadataGraphURI, "");
                
            } catch (Exception e) {
                throw new PipelineGraphManipulatorException(format(ERROR_LOAD_METADATAGRAPH_FROM_FILE), e);
            }
            
            graphStatus.checkResetPipelineRequest();
            try {
                WrappedResultSet rs = con.executeSelect(String.format(Locale.ROOT,
                        "SPARQL SELECT ?o WHERE { GRAPH <%s> {<%s> <%s> ?o}}",
                        metadataGraphURI, dataGraphURI, ODCS.dataBaseUrl));
                rs.next();
                dataBaseUrl = rs.getString(1);
            } catch (Exception e) {
                throw new PipelineGraphManipulatorException(format(ERROR_LOAD_METADATAGRAPH_DATABASE_URL), e);
            }
            
            graphStatus.checkResetPipelineRequest();
            File pvmRDFFile = new File(inputDirPath, uuid + "-pvm.rdf");
            if (pvmRDFFile.exists()) {
                LOG.info(format("Loading provenance metadata from rdfxml input file"));
                con.insertRdfXmlFromFile(pvmRDFFile, provenanceGraphURI, dataBaseUrl);
                con.execute(String.format(Locale.ROOT, "SPARQL INSERT INTO GRAPH <%s> { <%s> <%s> <%s> }", 
                        metadataGraphURI, dataGraphURI, ODCS.provenanceMetadataGraph, provenanceGraphURI));
            }
            
            graphStatus.checkResetPipelineRequest();
            File pvmTTLFile = new File(inputDirPath, uuid + "-pvm.ttl");
            if (pvmTTLFile.exists()) {
                LOG.info(format("Loading provenance metadata from ttl input file"));
                con.insertN3FromFile(pvmTTLFile, provenanceGraphURI, dataBaseUrl);
                con.execute(String.format(Locale.ROOT, "SPARQL INSERT INTO GRAPH <%s> { <%s> <%s> <%s> }", 
                        metadataGraphURI, dataGraphURI, ODCS.provenanceMetadataGraph, provenanceGraphURI));
            }
            
            graphStatus.checkResetPipelineRequest();
            File dRDFFile = new File(inputDirPath, uuid + "-d.rdf");
            if (dRDFFile.exists()) {
                LOG.info(format("Loading data from rdf input file"));
                con.insertRdfXmlFromFile(dRDFFile, dataGraphURI, dataBaseUrl);
                
            }
            
            graphStatus.checkResetPipelineRequest();
            File dTTLFile = new File(inputDirPath, uuid + "-d.ttl");
            if (dTTLFile.exists()) {
                LOG.info(format("Loading data from ttl input file"));
                con.insertN3FromFile(dTTLFile, dataGraphURI, dataBaseUrl);
            }
        } finally {
            if (con != null) {
                con.closeQuietly();
            }
        }
    }
    
    /**
     * Loads graphs into dirty database from clean database.
     * 
     * @throws Exception
     */
    private void loadGraphsIntoDirtyDBFromCleanDB() throws Exception {
        try {
            String[] graphs = getAllGraphNames();
                
            for (String graphName : graphs) {
                    
                File srcFile = null;
                File dstFile = null;
                OutputStreamWriter out = null;
                VirtuosoConnectionWrapper cleanConnection = null;
                VirtuosoConnectionWrapper dirtyConnection = null;
                try {
                    String tempFileName = generateRandomFileNameForGraph();
                    srcFile = new File(Engine.getCurrent().getCleanDBImportExportDir(), tempFileName);
                    dstFile = new File(Engine.getCurrent().getDirtyDBImportExportDir(), tempFileName);
                    
                    graphStatus.checkResetPipelineRequest();
                    cleanConnection = createCleanConnection();
                    LOG.info(String.format("Dumping data from clean db to ttl temporary file for graph %s", graphName));
                    String query = 
                            "CALL dump_graph_ttl('" + graphName + "', '" + srcFile.getAbsolutePath().replace("\\", "/") + "')";
                    cleanConnection.execute(query);
                    
                    graphStatus.checkResetPipelineRequest();
                    // move file if neccessary
                    if (!srcFile.getCanonicalFile().equals(dstFile.getCanonicalFile())) {
                        srcFile.renameTo(dstFile);
                    }
                    
                    graphStatus.checkResetPipelineRequest();
                    dirtyConnection = createDirtyConnection();
                    LOG.info(String.format("Loading data from ttl temporary file to dirty db for graph %s", graphName));
                    dirtyConnection.insertN3FromFile(dstFile, graphName, graphName);
                } finally {
                    if (dirtyConnection != null) {
                        dirtyConnection.closeQuietly();
                    }
                    if (cleanConnection != null) {
                        cleanConnection.closeQuietly();
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (Exception e) {
                            // do nothing
                        }
                    }
                    if (srcFile != null) {
                        safeDeleteFile(srcFile);
                    }
                    if (dstFile != null) {
                        safeDeleteFile(dstFile);
                    }
                }
            }
        } catch (Exception e) { 
            throw new PipelineGraphManipulatorException(format(ERROR_LOAD_GRAPHS_FROM_CLEAN_DB), e);
        }
    }

    /**
     * @return array with all graph names for current executing graph
     */
    private String[] getAllGraphNames() {
        String uuid = graphStatus.getUuid();
        
        ArrayList<String> graphs = new ArrayList<String>();
        graphs.add(graphStatus.getNamedGraphsPrefix() + ODCSInternal.dataGraphUriInfix + uuid);
        graphs.add(graphStatus.getNamedGraphsPrefix() + ODCSInternal.metadataGraphUriInfix + uuid);
        graphs.add(graphStatus.getNamedGraphsPrefix() + ODCSInternal.provenanceMetadataGraphUriInfix + uuid);
        graphs.addAll(graphStatus.getAttachedGraphs());
        return graphs.toArray(new String[0]);
    }
        
    /**
     * Delete file without causing any exceptions.
     * @param dirPath relative file path base
     * @param fileName file name
     */
    private void safeDeleteFile(String dirPath, String fileName) {
        try {
            if (dirPath == null) {
                LOG.debug("dirPath is null");
            }
            if (fileName == null) {
                LOG.debug("fileName is null");
            }
            File file = new File(dirPath, fileName);
            safeDeleteFile(file);
        } catch (Exception e) {
            LOG.error(format(ERROR_DELETE_FILE));
        }
    }
        
    /**
     * Delete file without causing any exceptions.
     * 
     * @param file file to delete
     */
    private void safeDeleteFile(File file) {
        try {
            if (file == null) {
                LOG.debug("file is null");
            }
            if (!file.delete() && file.exists()) {
                String message = String.format(ERROR_FILE_STILL_EXIST, file.getName());
                LOG.error(format(message));
            }
        } catch (Exception e) {
            LOG.error(format(ERROR_DELETE_FILE));
        }
    }
    
    
    /**
     * Generate random file name with temporary suffix.
     * 
     * @return random uuid
     */
    private String generateRandomFileNameForGraph() {
        return graphStatus.getUuid() + "-" + UUID.randomUUID() + "-temp.ttl";
    }

    /**
     * Create connection to dirty db for graph manipulations. 
     * 
     * @return VirtuosoConnectionWrapper object
     * @throws ConnectionException
     */
    private VirtuosoConnectionWrapper createDirtyConnection() throws ConnectionException {
        JDBCConnectionCredentials credit = ConfigLoader.getConfig().getEngineGroup().getDirtyDBJDBCConnectionCredentials();
        VirtuosoConnectionWrapper con = VirtuosoConnectionWrapper.createConnection(credit);
        con.adjustTransactionLevel(EnumLogLevel.AUTOCOMMIT);
        con.setQueryTimeout(0);
        return con;
    }
    
    /**
     * Create connection to clean db for graph manipulations. 
     * 
     * @return VirtuosoConnectionWrapper object
     * @throws ConnectionException
     */
    private VirtuosoConnectionWrapper createCleanConnection() throws ConnectionException {
        JDBCConnectionCredentials credit = ConfigLoader.getConfig().getEngineGroup().getCleanDBJDBCConnectionCredentials();
        VirtuosoConnectionWrapper con = VirtuosoConnectionWrapper.createConnection(credit);
        con.adjustTransactionLevel(EnumLogLevel.AUTOCOMMIT);
        con.setQueryTimeout(0);
        return con;
    }
        
    private String format(String message) {
        try {
            return FormatHelper.formatGraphMessage(message, graphStatus.getUuid(), graphStatus.isInCleanDbBeforeProcessing());
        } catch (Exception e) {
            return message;
        }
    }
}