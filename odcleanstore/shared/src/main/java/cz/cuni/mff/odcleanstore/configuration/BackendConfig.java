package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatInteger;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;

import java.util.Properties;

/**
 * Encapsulates Backend configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Backend configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class BackendConfig extends ConfigGroup {
    /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "backend" + NAME_DELIMITER;
    
    private final SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials;
    private final SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials;
    private final JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials;
    private final JDBCConnectionCredentials cleanDBJDBCConnectionCredentials;
    private final Integer queryTimeout;

    /**
     *
     * @param dirtyDBSparqlConnectionCredentials
     * @param cleanDBSparqlConnectionCredentials
     * @param dirtyDBJDBCConnectionCredentials
     * @param cleanDBJDBCConnectionCredentials
     * @param queryTimeout
     * @param dataGraphURIPrefix
     * @param metadataGraphURIPrefix
     * @param provenanceMetadataGraphURIPrefix
     */
    public BackendConfig(SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials,
            SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials,
            JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials, 
            JDBCConnectionCredentials cleanDBJDBCConnectionCredentials,
            Integer queryTimeout) {
        this.dirtyDBSparqlConnectionCredentials = dirtyDBSparqlConnectionCredentials;
        this.cleanDBSparqlConnectionCredentials = cleanDBSparqlConnectionCredentials;
        this.dirtyDBJDBCConnectionCredentials = dirtyDBJDBCConnectionCredentials;
        this.cleanDBJDBCConnectionCredentials = cleanDBJDBCConnectionCredentials;
        this.queryTimeout = queryTimeout;
    }

    /**
     * Extracts Backend configuration values from the given Properties instance. Returns a
     * BackendConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static BackendConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        
        SparqlEndpointConnectionCredentials dirtySparqlConnectionCredentials =
                loadSparqlEndpointConnectionCredentials(properties, EnumDbConnectionType.DIRTY, false);
        JDBCConnectionCredentials dirtyJDBCConnectionCredentials = 
                loadJDBCConnectionCredentials(properties,  EnumDbConnectionType.DIRTY);
        SparqlEndpointConnectionCredentials cleanSparqlConnectionCredentials =
                loadSparqlEndpointConnectionCredentials(properties,  EnumDbConnectionType.CLEAN, false);
        JDBCConnectionCredentials cleanJDBCConnectionCredentials =
                loadJDBCConnectionCredentials(properties,  EnumDbConnectionType.CLEAN);

        ParameterFormat<Integer> formatInteger = new FormatInteger();
        Integer queryTimeout = loadParam(properties, GROUP_PREFIX + "query_timeout", formatInteger);

        return new BackendConfig(
                dirtySparqlConnectionCredentials,
                cleanSparqlConnectionCredentials,
                dirtyJDBCConnectionCredentials,
                cleanJDBCConnectionCredentials,
                queryTimeout);
    }

    /**
     *
     * @return
     */
    public SparqlEndpointConnectionCredentials getDirtyDBSparqlConnectionCredentials() {
        return dirtyDBSparqlConnectionCredentials;
    }

    /**
     *
     * @return
     */
    public SparqlEndpointConnectionCredentials getCleanDBSparqlConnectionCredentials() {
        return cleanDBSparqlConnectionCredentials;
    }

    /**
     *
     * @return
     */
    public JDBCConnectionCredentials getDirtyDBJDBCConnectionCredentials() {
        return dirtyDBJDBCConnectionCredentials;
    }

    /**
     *
     * @return
     */
    public JDBCConnectionCredentials getCleanDBJDBCConnectionCredentials() {
        return cleanDBJDBCConnectionCredentials;
    }

    /**
     *
     * @return
     */
    public Integer getQueryTimeout() {
        return queryTimeout;
    }
}
