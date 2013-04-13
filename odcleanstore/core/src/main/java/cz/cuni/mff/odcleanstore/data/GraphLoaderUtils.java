package cz.cuni.mff.odcleanstore.data;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.EngineConfig;
import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.shared.SerializationLanguage;
import cz.cuni.mff.odcleanstore.shared.util.FileUtils;
import cz.cuni.mff.odcleanstore.shared.util.FileUtils.DirectoryException;

import java.io.File;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Utility methods for named graph import/export.
 * @author Jan Michelfeit
 */
public final class GraphLoaderUtils {
    private static final Pattern XML_PATTERN = Pattern.compile("^\\s*<(\\?xml|rdf:RDF)");

    /** Prefix of temporary files' names. */
    public static final String TMP_FILE_PREFIX = "odcs-";
    
    /** Flags for TTL import. */
    public static final int TTL_FLAGS = 64; // Relax TURTLE syntax to include popular violations

    /** Hide constructor for utility class. */
    private GraphLoaderUtils() {

    }
    
    /**
     * Guess serialization format of the given RDF data.
     * The detection is a heuristic.
     * @param src serialized RDF data
     * @return serialization type
     */
    public static SerializationLanguage guessLanguage(String src) {
        if (XML_PATTERN.matcher(src).find()) {
            return SerializationLanguage.RDFXML;
        } else {
            return SerializationLanguage.N3;
        }
    }

    /**
     * Returns import/export directory for the given Virtuoso database instance.
     * @param databaseInstance database instance
     * @param connection connection to database; MUST BE connection to database instance given in databaseInstance
     * @return temporary file
     * @throws QueryException error
     * @throws DirectoryException error
     * @see EngineConfig#getCleanImportExportDir()
     * @see EngineConfig#getDirtyImportExportDir()
     */
    public static File getImportExportDirectory(EnumDatabaseInstance databaseInstance, VirtuosoConnectionWrapper connection)
            throws QueryException, DirectoryException {
        
        EngineConfig config = ConfigLoader.getConfig().getEngineGroup();
        String relativeWDDir;
        switch (databaseInstance) {
        case CLEAN:
            relativeWDDir = config.getCleanImportExportDir();
            break;
        case DIRTY:
            relativeWDDir = config.getDirtyImportExportDir();
            break;
        default:
            throw new AssertionError();
        }

        String dirPath = FileUtils.satisfyDirectory(relativeWDDir, getServerRoot(connection));
        dirPath = dirPath.replace('\\', '/');
        File dir = new File(dirPath);

        return dir;
    }
    
    /**
     * Returns import/export directory for the given Virtuoso database instance.
     * @param databaseInstance database instance
     * @return temporary file
     * @throws DatabaseException error
     * @throws DirectoryException error
     * @see EngineConfig#getCleanImportExportDir()
     * @see EngineConfig#getDirtyImportExportDir()
     */
    public static File getImportExportDirectory(EnumDatabaseInstance databaseInstance)
            throws DatabaseException, DirectoryException {
        
        JDBCConnectionCredentials connectionCredentials;
        switch (databaseInstance) {
        case CLEAN:
            connectionCredentials = ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();
            break;
        case DIRTY:
            connectionCredentials = ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials();
            break;
        default:
            throw new AssertionError();
        }
        
        VirtuosoConnectionWrapper connection = null;
        File result = null;
        try {
            connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);
            result = getImportExportDirectory(databaseInstance, connection);
        } finally {
            if (connection != null) {
                connection.closeQuietly();
            }
        }
        return result;
    }
    
    /**
     * Returns Virtuoso server working directory.
     * @param connection connection to database
     * @return Virtuoso server working directory
     * @throws QueryException exception
     */
    private static String getServerRoot(VirtuosoConnectionWrapper connection) throws QueryException {
        WrappedResultSet resultSet = connection.executeSelect("SELECT server_root()");
        try {
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new QueryException(e);
        }
        return null;
    }
    
    /**
     * Insert RDF data from file in rdfXml format to the database.
     * @param connection database connection
     * @param sourceFile file with payload 
     * @param serialization serialization format of sourceFile
     * @param graphName name of the graph to insert
     * @param relativeBase relative URI base for payload
     * @throws ODCleanStoreException query error
     */
    public static void insertRdfFromFile(
            VirtuosoConnectionWrapper connection, File sourceFile, SerializationLanguage serialization,
            String graphName, String relativeBase)
            throws ODCleanStoreException {
        
        // Adjust transaction level - important, see Virtuoso manual, section 10.7
        EnumLogLevel originalLogLevel = connection.adjustTransactionLevel(EnumLogLevel.AUTOCOMMIT);

        String base = (relativeBase == null) ? "" : relativeBase;
        String escapedFileName = sourceFile.getAbsolutePath().replace('\\', '/');
        String query;
        switch (serialization) {
        case N3:
            query = String.format(
                    "CALL DB.DBA.TTLP(file_to_string_output('%s'), '%s', '%s', %d)",
                    escapedFileName,
                    base,
                    graphName,
                    TTL_FLAGS);
            break;
        case RDFXML:
            query = String.format(
                    "CALL DB.DBA.RDF_LOAD_RDFXML(file_to_string_output('%s'), '%s', '%s')",
                    escapedFileName,
                    base,
                    graphName);
            break;
        default:
            throw new ODCleanStoreException(
                    "Import of RDF data from file serialized in " + serialization + " is not supported");
        }

        try {
            connection.execute(query);
        } finally {
            if (originalLogLevel != null && originalLogLevel != EnumLogLevel.AUTOCOMMIT) {
                connection.adjustTransactionLevel(originalLogLevel);
            }
        }
    }
    
    /**
     * Exports a named graph to the given file in TTL format.
     * @param connection database connection
     * @param exportFile file to export to
     * @param graphName name of the graph to insert
     * @throws QueryException query error
     */
    public static void exportToTTL(VirtuosoConnectionWrapper connection, File exportFile, String graphName)
            throws QueryException {
        
        String escapedFileName = exportFile.getAbsolutePath().replace('\\', '/');
        String query = "CALL dump_graph_ttl('" + graphName + "', '" + escapedFileName + "')";
        connection.execute(query);
    }
}
