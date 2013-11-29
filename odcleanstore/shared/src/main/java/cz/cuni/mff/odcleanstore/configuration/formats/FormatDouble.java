package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

/**
 * A formatter to convert String values to Doubles.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatDouble extends ParameterFormat<Double> {
    @Override
    public Double convertValue(String paramName, String value) throws IllegalParameterFormatException {
        assert value != null && paramName != null;

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            throw new IllegalParameterFormatException(
                    "Parameter value [" + value + "] could not be converted to Double for param: " + paramName);
        }
    }
}
