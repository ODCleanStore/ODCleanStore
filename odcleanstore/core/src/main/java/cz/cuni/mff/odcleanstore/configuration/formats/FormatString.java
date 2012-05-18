package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

/**
 * A formatter to convert String values to Strings (e.g. a trivial one).
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatString extends ParameterFormat<String> {
    @Override
    public String convertValue(String groupName, String paramName, String value)
            throws IllegalParameterFormatException {
        return value;
    }
}
