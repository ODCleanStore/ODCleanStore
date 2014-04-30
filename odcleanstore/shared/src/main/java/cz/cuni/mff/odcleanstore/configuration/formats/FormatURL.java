package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * A formatter to convert String values to URLs.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatURL extends ParameterFormat<URL> {
    @Override
    public URL convertValue(String paramName, String value)
            throws IllegalParameterFormatException {
        assert value != null && paramName != null;

        try {
            return new URL(value);
        } catch (MalformedURLException e) {
            throw new IllegalParameterFormatException(
                    "Parameter value [" + value + "] could not be converted to URI for param: " + paramName);
        }
    }
}
