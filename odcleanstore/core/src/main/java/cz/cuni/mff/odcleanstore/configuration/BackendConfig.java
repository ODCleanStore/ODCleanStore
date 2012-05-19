package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatInteger;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURL;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.JDBCCoords;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointCoords;

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

    private SparqlEndpointCoords dirtyDBSparqlCoords;
    private SparqlEndpointCoords cleanDBSparqlCoords;
    private JDBCCoords dirtyDBJDBCCoords;
    private JDBCCoords cleanDBJDBCCoords;
    private Integer queryTimeout;
    private URI dataGraphURIPrefix;
    private URI metadataGraphURIPrefix;

    /**
     *
     * @param dirtyDBSparqlCoords
     * @param cleanDBSparqlCoords
     * @param dirtyDBJDBCCoords
     * @param cleanDBJDBCCoords
     * @param queryTimeout
     * @param dataGraphURIPrefix
     * @param metadataGraphURIPrefix
     */
    public BackendConfig(SparqlEndpointCoords dirtyDBSparqlCoords, SparqlEndpointCoords cleanDBSparqlCoords,
            JDBCCoords dirtyDBJDBCCoords, JDBCCoords cleanDBJDBCCoords, Integer queryTimeout,
            URI dataGraphURIPrefix, URI metadataGraphURIPrefix) {
        this.dirtyDBSparqlCoords = dirtyDBSparqlCoords;
        this.cleanDBSparqlCoords = cleanDBSparqlCoords;
        this.dirtyDBJDBCCoords = dirtyDBJDBCCoords;
        this.cleanDBJDBCCoords = cleanDBJDBCCoords;
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
        SparqlEndpointCoords dirtySparqlCoords = loadSparqlEndpointCoords(properties, DIRTY_DB_NAME);
        JDBCCoords dirtyJDBCCoords = loadJDBCCoords(properties, DIRTY_DB_NAME);

        SparqlEndpointCoords cleanSparqlCoords = loadSparqlEndpointCoords(properties, CLEAN_DB_NAME);
        JDBCCoords cleanJDBCCoords = loadJDBCCoords(properties, CLEAN_DB_NAME);

        ParameterFormat<Integer> formatInteger = new FormatInteger();
        Integer queryTimeout = loadParam(properties, "query_timeout", formatInteger);

        ParameterFormat<URI> formatURI = new FormatURI();
        URI dataGraphURIPrefix = loadParam(properties, "data_graph_uri_prefix", formatURI);
        URI metadataGraphURIPrefix = loadParam(properties, "metadata_graph_uri_prefix", formatURI);

        return new BackendConfig(
                dirtySparqlCoords,
                cleanSparqlCoords,
                dirtyJDBCCoords,
                cleanJDBCCoords,
                queryTimeout,
                dataGraphURIPrefix,
                metadataGraphURIPrefix);
    }

    /**
     * Extracts SPARQL Endpoint configuration values for the database given by its name
     * from the given Properties instance. Returns a SparqlEndpointCoords object instantiated
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
    private static SparqlEndpointCoords loadSparqlEndpointCoords(Properties properties, String dbName)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URL> formatURL = new FormatURL();

        URL url = loadParam(properties, dbName + "_sparql_endpoint_url", formatURL);

        return new SparqlEndpointCoords(url);
    }

    /**
     * Extracts JDBC configuration values for the database given by its name
     * from the given Properties instance. Returns a JDBCCoords object instantiated using
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
    private static JDBCCoords loadJDBCCoords(Properties properties, String dbName)
            throws ParameterNotAvailableException, IllegalParameterFormatException
    {
        ParameterFormat<URL> formatURL = new FormatURL();
        ParameterFormat<String> formatString = new FormatString();

        URL url = loadParam(properties, dbName + "_jdbc_url", formatURL);
        String username = loadParam(properties, dbName + "_jdbc_username", formatString);
        String password = loadParam(properties, dbName + "_jdbc_password", formatString);

        return new JDBCCoords(url, username, password);
    }

    /**
     *
     * @return
     */
    public SparqlEndpointCoords getDirtyDBSparqlCoords() {
        return dirtyDBSparqlCoords;
    }

    /**
     *
     * @return
     */
    public SparqlEndpointCoords getCleanDBSparqlCoords() {
        return cleanDBSparqlCoords;
    }

    /**
     *
     * @return
     */
    public JDBCCoords getDirtyDBJDBCCoords() {
        return dirtyDBJDBCCoords;
    }

    /**
     *
     * @return
     */
    public JDBCCoords getCleanDBJDBCCoords() {
        return cleanDBJDBCCoords;
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
