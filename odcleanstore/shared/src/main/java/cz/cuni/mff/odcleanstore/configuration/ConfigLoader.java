package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A globally accessible singleton Config instance.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public final class ConfigLoader {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigLoader.class);
    
    /** Default path to the configuration file. */
    private static final String DEFAULT_CONFIG_PATH = "odcs.ini";

    private static Config config;
    
    /**
     * Loads and parses the contents of the configuration file into a Config
     * instance. The instance is then available via the {@link #getConfig()} method.
     *
     * @param configPath path to the configuration file
     * @throws ConfigurationException
     */
    public static void loadConfig(String configPath) throws ConfigurationException {
        File configFile = new File(configPath);
        LOG.info("Loading global configuration from {}", configFile.getAbsolutePath());
        config = Config.load(configFile);
    }

    /**
     * Loads and parses the contents of the configuration file into a Config
     * instance. The instance is then available via the {@link #getConfig()} method.
     *
     * The configuration file is expected to be found at the standard location.
     *
     * @throws ConfigurationException
     */
    public static void loadConfig() throws ConfigurationException {
        loadConfig(DEFAULT_CONFIG_PATH);
    }

    /**
     * Returns the parsed configuration instance. <strong>Make sure to have
     * called {@link #loadConfig} prior to calling this method!</strong>
     *
     * @return
     */
    public static Config getConfig() {
        if (config == null) {
            throw new AssertionError("Configuration has not been loaded yet.");
        }

        return config;
    }
    
    
    /**
     * Returns whether the configuration has been loaded already.
     * @return true if the {@link #loadConfig()} has been called.
     */
    public static boolean isConfigLoaded() {
        return config != null;
    }
    
    /** Disable constructor for a utility class. */
    private ConfigLoader() {
    }
}
