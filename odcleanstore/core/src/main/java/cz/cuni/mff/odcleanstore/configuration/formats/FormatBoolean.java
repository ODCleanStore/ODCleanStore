package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

/**
 * A formatter to convert String values to Integers.
 *
 * @author Tomas Soukup
 *
 */
public class FormatBoolean extends ParameterFormat<Boolean> {
    @Override
    public Boolean convertValue(String paramName, String value)
            throws IllegalParameterFormatException {
        assert value != null && paramName != null;

        return Boolean.valueOf(value);
    }
}
