package cz.cuni.mff.odcleanstore.configuration;

import java.net.URI;
import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatLong;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

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
    /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "engine" + NAME_DELIMITER;
    
    private final JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials;
    private final JDBCConnectionCredentials cleanDBJDBCConnectionCredentials;
    private final Long startupTimeout;
    private final Long shutdownTimeout;
    private final Long lookForGraphInterval;
    private final Long secondCrashPenalty;
    private final String dirtyImportExportDir;
    private final String cleanImportExportDir;

    /**
     *
     * @param startupTimeout
     * @param shutdownTimeout
     */
    // CHECKSTYLE:OFF
    public EngineConfig(
            JDBCConnectionCredentials dirtyDBJDBCConnectionCredentials, 
            JDBCConnectionCredentials cleanDBJDBCConnectionCredentials,
            Long startupTimeout,
            Long shutdownTimeout,
            Long lookForGraphInterval,
            Long secondCrashPenalty,
            String dirtyImportExportDir,
            String cleanImportExportDir) {
        this.dirtyDBJDBCConnectionCredentials = dirtyDBJDBCConnectionCredentials;
        this.cleanDBJDBCConnectionCredentials = cleanDBJDBCConnectionCredentials;
        this.startupTimeout = startupTimeout;
        this.shutdownTimeout = shutdownTimeout;
        this.lookForGraphInterval = lookForGraphInterval;
        this.secondCrashPenalty = secondCrashPenalty;
        this.dirtyImportExportDir = dirtyImportExportDir;
        this.cleanImportExportDir = cleanImportExportDir;
    }
    
    // CHECKSTYLE:ON

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
        JDBCConnectionCredentials dirtyJDBCConnectionCredentials = 
                loadJDBCConnectionCredentials(properties,  EnumDbConnectionType.DIRTY);
        JDBCConnectionCredentials cleanJDBCConnectionCredentials =
                loadJDBCConnectionCredentials(properties,  EnumDbConnectionType.CLEAN);
        
        ParameterFormat<Long> formatLong = new FormatLong();
        Long startupTimeout = loadParam(properties, GROUP_PREFIX + "startup_timeout", formatLong);
        Long shutdownTimeout = loadParam(properties, GROUP_PREFIX + "shutdown_timeout", formatLong);
        Long lookForGraphInterval = loadParam(properties, GROUP_PREFIX + "look_for_graph_interval", formatLong);
        Long secondCrashPenalty = loadParam(properties, GROUP_PREFIX + "second_crash_penalty", formatLong);
        
        ParameterFormat<String> formatString = new FormatString();
        String dirtyImportExportDir = loadParam(properties, GROUP_PREFIX + "dirty_import_export_dir", formatString);
        String cleanImportExportDir = loadParam(properties, GROUP_PREFIX + "clean_import_export_dir", formatString);
        
        return new EngineConfig(
                dirtyJDBCConnectionCredentials,
                cleanJDBCConnectionCredentials,
                startupTimeout,
                shutdownTimeout,
                lookForGraphInterval,
                secondCrashPenalty,
                dirtyImportExportDir,
                cleanImportExportDir);
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

  /**
  *
  * @return Gets directory for dirty db import export files.
  */
  public String getDirtyImportExportDir() {
      return dirtyImportExportDir;
  }

  /**
  *
  * @return Gets directory for clean db import export files.
  */
  public String getCleanImportExportDir() {
      return cleanImportExportDir;
  }
}
