package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * A globally accessible singleton Config instance.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ConfigLoader {
    /** path to the configuration file */
    private static final String CONFIG_PATH = "odcs.ini";

    private static Config config;

    /**
     * Loads and parses the contents of the configuration file into a Config
     * instance. The instance is then available via the {@link #getConfig()} method.
     *
     * The configuration file is expected to be found at the standard location.
     *
     * @throws ConfigurationException
     */
    public static void loadConfig() throws ConfigurationException {
        File f = new File(CONFIG_PATH);
        LoggerFactory.getLogger(ConfigLoader.class).info(f.getAbsolutePath());
        config = Config.load(new File(CONFIG_PATH));
    }

    /**
     * Returns the parsed configuration instance. <strong>Make sure to have
     * called {@load #loadConfig} prior to calling this method!</strong>
     *
     * @return
     */
    public static Config getConfig() {
        if (config == null) {
            throw new AssertionError("Configuration has not been loaded yet.");
        }

        return config;
    }
}
