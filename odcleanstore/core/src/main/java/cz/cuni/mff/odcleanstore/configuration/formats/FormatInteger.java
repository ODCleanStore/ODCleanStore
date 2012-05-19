package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

/**
 * A formatter to convert String values to Integers.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatInteger extends ParameterFormat<Integer> {
    @Override
    public Integer convertValue(String groupName, String paramName, String value)
            throws IllegalParameterFormatException {
        assert groupName != null && paramName != null;
        assert value != null;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalParameterFormatException(
                    "Parameter value [" + value + "] could not be converted to Integer for group/param: "
                     + groupName + "/" + paramName);
        }
    }
}
