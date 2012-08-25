package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

import java.util.Properties;

/**
 * An abstract class for all configuration groups. Subclass this class in order
 * to create a new configuration group.
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
// TODO:
// Vytvorit spolecnou kolekci nainstanciovanych format objektu
// namisto vytvareni potrebnych formatu v kazde load metode?
public abstract class ConfigGroup {
    /** The name of the represented group formatted as it occurs in the properties. */
    protected static String GROUP_NAME;

    /** The delimiter between the group name and property name in the properties. */
    protected static String NAME_DELIMITER = ".";

    /**
     * Loads the value of the parameter denoted by the represented group name
     * and the given param-name and converts according to the given format.
     *
     * @param properties
     * @param paramName
     * @param format
     * @return
     * @throws ParameterNotAvailableException if the requested parameter does not occur in the
     *         given properties instance
     * @throws IllegalParameterFormatException if the requested parameter occurs but cannot
     *         be converted to the given type
     */
    protected static <T> T loadParam(Properties properties, String paramName, ParameterFormat<T> format)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        String value = properties.getProperty(GROUP_NAME + NAME_DELIMITER + paramName);
        if (value == null) {
            throw new ParameterNotAvailableException("Parameter not available (group/parameter): "
                    + GROUP_NAME + "/" + paramName);
        }

        return format.convertValue(GROUP_NAME, paramName, value);
    }
}
