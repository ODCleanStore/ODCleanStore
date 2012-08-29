package cz.cuni.mff.odcleanstore.configuration;

import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatLong;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

/**
 * Encapsulates Pipeline configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Pipeline configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Petr Jerman
 *
 */
public class PipelineConfig extends ConfigGroup {
    
    static
    {
        GROUP_NAME = "pipeline";
    }

    
    private Long lookForGraphInterval;
    private Long secondCrashPenalty;

    /**
     *
     * @param lookForGraphInterval
     * @param secondCrashPenalty
     */
    public PipelineConfig(
    		Long lookForGraphInterval,
    		Long secondCrashPenalty) {
    	this.lookForGraphInterval = lookForGraphInterval;
        this.secondCrashPenalty = secondCrashPenalty;
    }

    /**
     * Extracts Pipeline configuration values from the given Properties instance.
     * Returns a Pipeline object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static PipelineConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException
    {
    	ParameterFormat<Long> formatLong = new FormatLong();
        Long lookForGraphInterval = loadParam(properties, "look_for_graph_interval", formatLong);
        Long secondCrashPenalty = loadParam(properties, "second_crash_penalty", formatLong);
        
        return new PipelineConfig(
        		lookForGraphInterval,
        		secondCrashPenalty);
    }
   
    /**
    *
    * @return
    */
   public Long getLookForGraphInterval() {
       return lookForGraphInterval;
   }
   
   /**
   *
   * @return
   */
  public Long getSecondCrashPenalty() {
      return secondCrashPenalty;
  }

}
