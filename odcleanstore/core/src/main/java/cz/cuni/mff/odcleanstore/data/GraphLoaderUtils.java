package cz.cuni.mff.odcleanstore.data;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.configuration.EngineConfig;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.SerializationLanguage;
import cz.cuni.mff.odcleanstore.shared.util.FileUtils;
import cz.cuni.mff.odcleanstore.shared.util.FileUtils.DirectoryException;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Utility methods for named graph import/export.
 * @author Jan Michelfeit
 */
public final class GraphLoaderUtils {
    private static final Pattern XML_PATTERN = Pattern.compile("^\\s*<(\\?xml|rdf:RDF)");

    /** Prefix of temporary files' names. */
    public static final String TMP_FILE_PREFIX = "odcs-";

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
     * Returns unique temporary file in import/export directory.
     * @param connection connection to database; MUST BE connection to database instance given in databaseInstance
     * @param databaseInstance database instance
     * @return temporary file
     * @throws QueryException error
     * @throws DirectoryException error
     * @throws IOException error
     * @see EngineConfig#getCleanImportExportDir()
     * @see EngineConfig#getDirtyImportExportDir()
     */
    public static File getImportExportTmpFile(VirtuosoConnectionWrapper connection, EnumDatabaseInstance databaseInstance)
            throws QueryException, DirectoryException, IOException {
        
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

        String dirPath = FileUtils.satisfyDirectory(relativeWDDir, connection.getServerRoot());
        dirPath = dirPath.replace('\\', '/');
        File dir = new File(dirPath);

        File tempFile = File.createTempFile(TMP_FILE_PREFIX, null, dir);
        // ? tempFile.deleteOnExit();
        return tempFile;
    }
}
