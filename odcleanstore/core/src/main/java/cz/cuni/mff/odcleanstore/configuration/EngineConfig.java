package cz.cuni.mff.odcleanstore.configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatLong;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
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
    
    public static final String DATA_GRAPH_PREFIX = "http://opendata.cz/infrastructure/odcleanstore/data/";
    public static final String METADATA_GRAPH_PREFIX = "http://opendata.cz/infrastructure/odcleanstore/metadata/";
    public static final String PROVENANCE_METADATA_GRAPH_PREFIX = "http://opendata.cz/infrastructure/odcleanstore/provenanceMetadata/";
    
    private final URI dataGraphURIPrefix;
    private final URI metadataGraphURIPrefix;
    private final URI provenanceMetadataGraphURIPrefix;
    
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
            URI dataGraphURIPrefix, 
            URI metadataGraphURIPrefix,
            URI provenanceMetadataGraphURIPrefix,
            Long lookForGraphInterval,
            Long secondCrashPenalty,
            String dirtyImportExportDir,
            String cleanImportExportDir) {
        this.dirtyDBJDBCConnectionCredentials = dirtyDBJDBCConnectionCredentials;
        this.cleanDBJDBCConnectionCredentials = cleanDBJDBCConnectionCredentials;
        this.startupTimeout = startupTimeout;
        this.shutdownTimeout = shutdownTimeout;
        this.dataGraphURIPrefix = dataGraphURIPrefix;
        this.metadataGraphURIPrefix = metadataGraphURIPrefix;
        this.provenanceMetadataGraphURIPrefix = provenanceMetadataGraphURIPrefix;
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
     * @throws URISyntaxException 
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
        
        
        URI dataGraphURIPrefix;
		try { dataGraphURIPrefix = new URI(DATA_GRAPH_PREFIX); }
    	catch (URISyntaxException e) { throw new IllegalParameterFormatException("dataGraphURIPrefix"); }
        
		URI metadataGraphURIPrefix;
		try { metadataGraphURIPrefix = new URI(METADATA_GRAPH_PREFIX); }
		catch (URISyntaxException e) { throw new IllegalParameterFormatException("metadataGraphURIPrefix"); }
		
		URI provenanceMetadataGraphURIPrefix;
		try { provenanceMetadataGraphURIPrefix = new URI(PROVENANCE_METADATA_GRAPH_PREFIX); }
		catch (URISyntaxException e) { throw new IllegalParameterFormatException("provenanceMetadataGraphURIPrefix"); }
       
        ParameterFormat<String> formatString = new FormatString();
        String dirtyImportExportDir = loadParam(properties, GROUP_PREFIX + "dirty_import_export_dir", formatString);
        String cleanImportExportDir = loadParam(properties, GROUP_PREFIX + "clean_import_export_dir", formatString);
        
        return new EngineConfig(
                dirtyJDBCConnectionCredentials,
                cleanJDBCConnectionCredentials,
                startupTimeout,
                shutdownTimeout,
                dataGraphURIPrefix,
                metadataGraphURIPrefix,
                provenanceMetadataGraphURIPrefix,
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

    /**
     * 
     * @return
     */
    public URI getProvenanceMetadataGraphURIPrefix() {
        return provenanceMetadataGraphURIPrefix;
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
