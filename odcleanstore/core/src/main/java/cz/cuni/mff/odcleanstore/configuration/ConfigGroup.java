package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURL;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;

import java.net.URL;
import java.util.Properties;

/**
 * An abstract class for all configuration groups. Subclass this class in order
 * to create a new configuration group.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
// TODO:
// Vytvorit spolecnou kolekci nainstanciovanych format objektu
// namisto vytvareni potrebnych formatu v kazde load metode?
public abstract class ConfigGroup {
    /** The delimiter between the group name and property name in the properties. */
    protected static final String NAME_DELIMITER = ".";
    
    /** Type of database. */
    protected enum EnumDbConnectionType {
        /** Dirty database. */
        DIRTY("db.dirty."),
        
        /** Dirty database - authorized for SPARQL UPDATE. */
        DIRTY_UPDATE("db.dirty_update."),
        
        /** Clean database. */
        CLEAN("db.clean.");
        
        private final String prefix;
        
        private EnumDbConnectionType(String prefix) {
            this.prefix = prefix;
        }
        
        public String getConfigPrefix() {
            return prefix;
        }
    }
    
    /** Database-name prefix for configuration values related to the dirty database. */
    protected static final String DIRTY_DB_NAME = "dirty";

    /** Database-name prefix for configuration values related to the clean database. */
    protected static final String CLEAN_DB_NAME = "clean";

    /**
     * Loads the value of the parameter denoted by the represented group name
     * and the given param-name and converts according to the given format.
     *
     * @param properties
     * @param paramName
     * @param format
     * @return
     * @throws ParameterNotAvailableException if the requested parameter does not occur in the
     *         given properties instance
     * @throws IllegalParameterFormatException if the requested parameter occurs but cannot
     *         be converted to the given type
     */
    protected static <T> T loadParam(Properties properties, String paramName, ParameterFormat<T> format)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        String value = properties.getProperty(paramName);
        if (value == null) {
            throw new ParameterNotAvailableException("Parameter not available: " + paramName);
        }

        return format.convertValue(paramName, value);
    }
    
    /**
     * Extracts JDBC configuration values for the database given by its name
     * from the given Properties instance. Returns a JDBCConnectionCredentials object instantiated using
     * the extracted values.
     *
     * @param properties
     * @param dbName
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    protected static JDBCConnectionCredentials loadJDBCConnectionCredentials(Properties properties, EnumDbConnectionType dbType)
            throws ParameterNotAvailableException, IllegalParameterFormatException
    {
        ParameterFormat<String> formatString = new FormatString();
        
        String connectionString = loadParam(properties, 
        		dbType.getConfigPrefix() + "jdbc.connection_string", formatString);
        String username = loadParam(properties, dbType.getConfigPrefix() + "jdbc.username", formatString);
        String password = loadParam(properties, dbType.getConfigPrefix() + "jdbc.password", formatString);

        return new JDBCConnectionCredentials(connectionString, username, password);
    }
    
    /**
     * Extracts SPARQL Endpoint configuration values for the database given by its name
     * from the given Properties instance. Returns a SparqlEndpointConnectionCredentials object instantiated
     * using the extracted values.
     *
     * @param properties
     * @param dbName
     * @param requireAuth if false, only endpoint URL is loaded, otherwise username and password are also required
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    protected static SparqlEndpointConnectionCredentials loadSparqlEndpointConnectionCredentials(
            Properties properties, EnumDbConnectionType dbType, boolean requireAuth)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        
        ParameterFormat<URL> formatURL = new FormatURL();
        URL url = loadParam(properties, dbType.getConfigPrefix() + "sparql.endpoint_url", formatURL);
        if (requireAuth) {
            ParameterFormat<String> formatString = new FormatString();
            String username = loadParam(properties, dbType.getConfigPrefix() + "sparql.endpoint_username", formatString);
            String password = loadParam(properties, dbType.getConfigPrefix() + "sparql.endpoint_password", formatString);
            return new SparqlEndpointConnectionCredentials(url, username, password);
        } else {
            return new SparqlEndpointConnectionCredentials(url);
        }
    }
}
