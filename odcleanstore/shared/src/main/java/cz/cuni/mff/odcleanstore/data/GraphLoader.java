package cz.cuni.mff.odcleanstore.data;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.EngineConfig;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.core.ODCSUtils;
import cz.cuni.mff.odcleanstore.core.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.shared.util.FileUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Utility class for loading serialized RDF data to Virtuoso.
 * @author Jan Michelfeit
 */
public class GraphLoader {
    private static final Logger LOG = LoggerFactory.getLogger(GraphLoader.class);
    
    
    private final EnumDatabaseInstance databaseInstance;

    /**
     * Constructor.
     * @param databaseInstance database instance to import to
     */
    public GraphLoader(EnumDatabaseInstance databaseInstance) {
        this.databaseInstance = databaseInstance;
    }

    /**
     * Import graph serialized as N3 or RDF/XML to database into the given named graph. 
     * @param contents RDF data serialized as N3 or RDF/XML
     * @param graphURI URI of graph to import to
     * @throws ODCleanStoreException error
     */
    public void importGraph(final String contents, String graphURI) throws ODCleanStoreException {
        String src = FileUtils.removeInitialBOMXml(contents);

        VirtuosoConnectionWrapper connection = null;
        Writer outputWriter = null;
        File tmpFile = null;
        try {
            connection = createConnection();
            connection.setQueryTimeout(0);

            File importExportDir = GraphLoaderUtils.getImportExportDirectory(databaseInstance, connection);
            tmpFile = File.createTempFile(GraphLoaderUtils.TMP_FILE_PREFIX, null, importExportDir);
            outputWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpFile), ODCSUtils.DEFAULT_ENCODING));
            outputWriter.write(src);
            outputWriter.close();
            
            GraphLoaderUtils.insertRdfFromFile(connection, tmpFile, GraphLoaderUtils.guessLanguage(src), graphURI, "");
        } catch (IOException e) {
            LOG.error("Error with temporary file when importing graph " + graphURI, e);
            throw new ODCleanStoreException(e);
        } catch (ODCleanStoreException e) {
            LOG.error("Error when importing graph " + graphURI, e);
            throw e;
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            if (tmpFile != null) {
                tmpFile.delete();
            }
            if (connection != null) {
                connection.closeQuietly();
            }
        }
    }

    private VirtuosoConnectionWrapper createConnection() throws ConnectionException {
        EngineConfig config = ConfigLoader.getConfig().getEngineGroup();
        switch (databaseInstance) {
        case CLEAN:
            return VirtuosoConnectionFactory.createJDBCConnection(config.getCleanDBJDBCConnectionCredentials());
        case DIRTY:
            return VirtuosoConnectionFactory.createJDBCConnection(config.getDirtyDBJDBCConnectionCredentials());
        default:
            throw new AssertionError();
        }
    }
}
