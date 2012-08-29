package cz.cuni.mff.odcleanstore.configuration;

import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatLong;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

/**
 * Encapsulates Engine configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Engine configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Petr Jerman
 *
 */
public class EngineConfig extends ConfigGroup {
    
    static
    {
        GROUP_NAME = "engine";
    }

    
    private Long startupTimeout;
    private Long shutdownTimeout;

    /**
     *
     * @param startupTimeout
     * @param shutdownTimeout
     */
    public EngineConfig(
    		Long startupTimeout,
    		Long shutdownTimeout) {
    	this.startupTimeout = startupTimeout;
        this.shutdownTimeout = shutdownTimeout;
    }

    /**
     * Extracts Engine configuration values from the given Properties instance.
     * Returns a Engine object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static EngineConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException
    {
    	ParameterFormat<Long> formatLong = new FormatLong();
        Long startupTimeout = loadParam(properties, "startup_timeout", formatLong);
        Long shutdownTimeout = loadParam(properties, "shutdown_timeout", formatLong);
        
        return new EngineConfig(
        		startupTimeout,
        		shutdownTimeout);
    }
   
    /**
    *
    * @return
    */
   public Long getStartupTimeout() {
       return startupTimeout;
   }
   
   /**
   *
   * @return
   */
  public Long getShutdownTimeout() {
      return shutdownTimeout;
  }

}
