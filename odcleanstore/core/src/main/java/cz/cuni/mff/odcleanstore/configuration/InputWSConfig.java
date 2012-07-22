package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURL;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointConnectionCredentials;

import java.io.File;
import java.net.URL;
import java.util.Properties;

/**
 * Encapsulates InputWS configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract InputWS configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class InputWSConfig extends ConfigGroup {
	/** database-name prefix for configuration values related to the dirty database */
    private static final String DIRTY_DB_NAME = "dirty";

    /** database-name prefix for configuration values related to the clean database */
    private static final String CLEAN_DB_NAME = "clean";
    
    static
    {
        GROUP_NAME = "input_ws";
    }

    private URL endpointURL;
    // TODO: doresit jak se konfiguruje instalacni adresar
    private String inputDirPath;
    private SparqlEndpointConnectionCredentials sparqlEndpointConnectionCredentials;
    private JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials;
    private JDBCConnectionCredentials cleanDBJDBCConnectionCredentials;

    /**
     *
     * @param endpointURL
     * @param inputDirPath
     * @param sparqlEndpointConnectionCredentials
     * @param dirtyDBJDBCConnectionCredentials
     * @param cleanDBJDBCConnectionCredentials
     */
    public InputWSConfig(
    		URL endpointURL,
    		String inputDirPath, 
    		SparqlEndpointConnectionCredentials sparqlEndpointConnectionCredentials, 
    		JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials, 
            JDBCConnectionCredentials cleanDBJDBCConnectionCredentials) {
    	this.endpointURL = endpointURL;
        this.inputDirPath = inputDirPath;
        this.sparqlEndpointConnectionCredentials = sparqlEndpointConnectionCredentials;
        this.dirtyDBJDBCConnectionCredentials = dirtyDBJDBCConnectionCredentials;
        this.cleanDBJDBCConnectionCredentials = cleanDBJDBCConnectionCredentials;
    }

    /**
     * Extracts InputWS configuration values from the given Properties instance.
     * Returns a InputWSConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static InputWSConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException
    {
        ParameterFormat<String> formatString = new FormatString();
        String inputDirPath = loadParam(properties, "input_dir_path", formatString);
        if(!inputDirPath.endsWith(File.separator)) {
        	inputDirPath = inputDirPath +  File.separator;
        }

        ParameterFormat<URL> formatURL = new FormatURL();
        URL endpointURL = loadParam(properties, "endpoint_url", formatURL);
        
        JDBCConnectionCredentials dirtyJDBCConnectionCredentials = loadJDBCConnectionCredentials(properties, DIRTY_DB_NAME);

        JDBCConnectionCredentials cleanJDBCConnectionCredentials = loadJDBCConnectionCredentials(properties, CLEAN_DB_NAME);

        return new InputWSConfig(
        		endpointURL,
                inputDirPath,
                new SparqlEndpointConnectionCredentials(endpointURL),
                dirtyJDBCConnectionCredentials,
                cleanJDBCConnectionCredentials);
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
            throws ParameterNotAvailableException, IllegalParameterFormatException {
    	
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
    public URL getEndpointURL() {
    	return endpointURL;
    }
    
    /**
     *
     * @return
     */
    public String getInputDirPath() {
        return inputDirPath;
    }

    /**
     *
     * @return
     */
    public SparqlEndpointConnectionCredentials getSparqlEndpointConnectionCredentials() {
        return sparqlEndpointConnectionCredentials;
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
}
