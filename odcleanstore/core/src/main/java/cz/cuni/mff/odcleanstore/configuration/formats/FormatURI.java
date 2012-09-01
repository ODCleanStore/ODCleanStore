package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * A formatter to convert String values to URIs.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatURI extends ParameterFormat<URI> {
    @Override
    public URI convertValue(String paramName, String value)
            throws IllegalParameterFormatException {
        assert value != null && paramName != null;

        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw new IllegalParameterFormatException(
                    "Parameter value [" + value + "] could not be converted to URI for param: " + paramName);
        }
    }
}
