package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURL;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

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
    /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "input_ws" + NAME_DELIMITER;
    
    private URL endpointURL;
    private URL namedGraphsPrefix;

    /**
     *
     * @param endpointURL
     * @param inputDirPath
     */
    public InputWSConfig(URL endpointURL, URL namedGraphsPrefix) {
        this.endpointURL = endpointURL;
        this.namedGraphsPrefix = namedGraphsPrefix;
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
        ParameterFormat<URL> formatURL = new FormatURL();
        URL endpointURL = loadParam(properties, GROUP_PREFIX + "endpoint_url", formatURL);
        URL namedGraphsPrefix = loadParam(properties, GROUP_PREFIX + "named_graphs_prefix", formatURL);

        return new InputWSConfig(endpointURL, namedGraphsPrefix);
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
   public URL getNamedGraphsPrefix() {
       return namedGraphsPrefix;
   }
}
