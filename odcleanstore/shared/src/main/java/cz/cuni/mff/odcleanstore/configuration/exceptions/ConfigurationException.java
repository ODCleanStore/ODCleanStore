package cz.cuni.mff.odcleanstore.configuration.exceptions;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ConfigurationException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     * @param message message
     */
    public ConfigurationException(String message) {
        super("Configuration error: " + message);
    }
}
