package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

/**
 * A formatter to convert String values to Longs.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatLong extends ParameterFormat<Long> {
    @Override
    public Long convertValue(String paramName, String value)
            throws IllegalParameterFormatException {
        assert value != null && paramName != null;

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalParameterFormatException(
                    "Parameter value [" + value + "] could not be converted to Long for param: " + paramName);
        }
    }
}
