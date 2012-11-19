package cz.cuni.mff.odcleanstore.configuration.formats;

import java.util.UUID;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

/**
 * A formatter to convert String values to UUIDs.
 *
 * @author Petr Jerman (petr.jerman@gmail.com)
 *
 */
public class FormatUUID extends ParameterFormat<UUID> {
    @Override
    public UUID convertValue(String paramName, String value)
            throws IllegalParameterFormatException {
        assert value != null && paramName != null;

        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalParameterFormatException(
                    "Parameter value [" + value + "] could not be converted to UUID for param: " + paramName);
        }
    }
}
