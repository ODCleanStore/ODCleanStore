package cz.cuni.mff.odcleanstore.configuration.exceptions;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ParameterNotAvailableException extends ConfigurationException {
    private static final long serialVersionUID = 1L;

    /**
     * Create a new instance.
     * @param message message
     */
    public ParameterNotAvailableException(String message) {
        super(message);
    }
}
