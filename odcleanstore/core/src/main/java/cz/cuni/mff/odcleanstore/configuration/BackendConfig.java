package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatInteger;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURL;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;

import java.net.URI;
import java.net.URL;
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
    /** database-name prefix for configuration values related to the dirty database */
    private static final String DIRTY_DB_NAME = "dirty";

    /** database-name prefix for configuration values related to the clean database */
    private static final String CLEAN_DB_NAME = "clean";

    static
    {
        GROUP_NAME = "backend";
    }

    private SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials;
    private SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials;
    private JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials;
    private JDBCConnectionCredentials cleanDBJDBCConnectionCredentials;
    private Integer queryTimeout;
    private URI dataGraphURIPrefix;
    private URI metadataGraphURIPrefix;

    /**
     *
     * @param dirtyDBSparqlConnectionCredentials
     * @param cleanDBSparqlConnectionCredentials
     * @param dirtyDBJDBCConnectionCredentials
     * @param cleanDBJDBCConnectionCredentials
     * @param queryTimeout
     * @param dataGraphURIPrefix
     * @param metadataGraphURIPrefix
     */
    public BackendConfig(SparqlEndpointConnectionCredentials dirtyDBSparqlConnectionCredentials,
    		SparqlEndpointConnectionCredentials cleanDBSparqlConnectionCredentials,
            JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials, 
            JDBCConnectionCredentials cleanDBJDBCConnectionCredentials, Integer queryTimeout,
            URI dataGraphURIPrefix, URI metadataGraphURIPrefix) {
        this.dirtyDBSparqlConnectionCredentials = dirtyDBSparqlConnectionCredentials;
        this.cleanDBSparqlConnectionCredentials = cleanDBSparqlConnectionCredentials;
        this.dirtyDBJDBCConnectionCredentials = dirtyDBJDBCConnectionCredentials;
        this.cleanDBJDBCConnectionCredentials = cleanDBJDBCConnectionCredentials;
        this.queryTimeout = queryTimeout;
        this.dataGraphURIPrefix = dataGraphURIPrefix;
        this.metadataGraphURIPrefix = metadataGraphURIPrefix;
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
        SparqlEndpointConnectionCredentials dirtySparqlConnectionCredentials = loadSparqlEndpointConnectionCredentials(properties, DIRTY_DB_NAME);
        JDBCConnectionCredentials dirtyJDBCConnectionCredentials = loadJDBCConnectionCredentials(properties, DIRTY_DB_NAME);

        SparqlEndpointConnectionCredentials cleanSparqlConnectionCredentials = loadSparqlEndpointConnectionCredentials(properties, CLEAN_DB_NAME);
        JDBCConnectionCredentials cleanJDBCConnectionCredentials = loadJDBCConnectionCredentials(properties, CLEAN_DB_NAME);

        ParameterFormat<Integer> formatInteger = new FormatInteger();
        Integer queryTimeout = loadParam(properties, "query_timeout", formatInteger);

        ParameterFormat<URI> formatURI = new FormatURI();
        URI dataGraphURIPrefix = loadParam(properties, "data_graph_uri_prefix", formatURI);
        URI metadataGraphURIPrefix = loadParam(properties, "metadata_graph_uri_prefix", formatURI);

        return new BackendConfig(
                dirtySparqlConnectionCredentials,
                cleanSparqlConnectionCredentials,
                dirtyJDBCConnectionCredentials,
                cleanJDBCConnectionCredentials,
                queryTimeout,
                dataGraphURIPrefix,
                metadataGraphURIPrefix);
    }

    /**
     * Extracts SPARQL Endpoint configuration values for the database given by its name
     * from the given Properties instance. Returns a SparqlEndpointConnectionCredentials object instantiated
     * using the extracted values.
     *
     * It is expected that the configuration values are given in the following format:
     *
     * [group_name].[db_name][param_name] = [param_value]
     *
     * @param properties
     * @param dbName
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    private static SparqlEndpointConnectionCredentials loadSparqlEndpointConnectionCredentials(Properties properties, String dbName)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URL> formatURL = new FormatURL();

        URL url = loadParam(properties, dbName + "_sparql_endpoint_url", formatURL);

        return new SparqlEndpointConnectionCredentials(url);
    }

    /**
     * Extracts JDBC configuration values for the database given by its name
     * from the given Properties instance. Returns a JDBCConnectionCredentials object instantiated using
     * the extracted values.
     *
     * It is expected that the configuration values are given in the following format:
     *
     * [group_name].[db_name][param_name] = [param_value]
     *
     * @param properties
     * @param dbName
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    private static JDBCConnectionCredentials loadJDBCConnectionCredentials(Properties properties, String dbName)
            throws ParameterNotAvailableException, IllegalParameterFormatException
    {
        ParameterFormat<String> formatString = new FormatString();

        String connectionString = loadParam(properties, dbName + "_jdbc_connection_string", formatString);
        String username = loadParam(properties, dbName + "_jdbc_username", formatString);
        String password = loadParam(properties, dbName + "_jdbc_password", formatString);

        return new JDBCConnectionCredentials(connectionString, username, password);
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

    /**
     *
     * @return
     */
    public URI getDataGraphURIPrefix() {
        return dataGraphURIPrefix;
    }

    /**
     *
     * @return
     */
    public URI getMetadataGraphURIPrefix() {
        return metadataGraphURIPrefix;
    }
}
