package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatLong;
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
    
    private final URL endpointURL;
    private final URL namedGraphsPrefix;
    private final Long recoveryCrashPenalty;

    /**
     *
     * @param endpointURL
     * @param inputDirPath
     * @param recoveryCrashPenalty
     */
    public InputWSConfig(URL endpointURL, URL namedGraphsPrefix, Long recoveryCrashPenalty) {
        this.endpointURL = endpointURL;
        this.namedGraphsPrefix = namedGraphsPrefix;
        this.recoveryCrashPenalty = recoveryCrashPenalty;
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
        ParameterFormat<Long> formatLong = new FormatLong();
        
        URL endpointURL = loadParam(properties, GROUP_PREFIX + "endpoint_url", formatURL);
        URL namedGraphsPrefix = loadParam(properties, GROUP_PREFIX + "named_graphs_prefix", formatURL);
        Long recoveryCrashPenalty = loadParam(properties, GROUP_PREFIX + "recovery_crash_penalty", formatLong);

        return new InputWSConfig(endpointURL, namedGraphsPrefix, recoveryCrashPenalty);
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
   
   /**
   *
   * @return
   */
  public Long getRecoveryCrashPenalty() {
      return recoveryCrashPenalty;
  }
}
