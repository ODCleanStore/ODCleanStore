package cz.cuni.mff.odcleanstore.configuration.formats;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

/**
 * An abstract class for all value formatters. Subclass this class in order to implement
 * a new formatter.
 *
 * @param <T> Type of the converted value
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 */
public abstract class ParameterFormat<T> {
    /**
     * Converts the given String value of the given group/parameter to the represented format.
     *
     * @param groupName configuration group
     * @param paramName configuration parameter name
     * @param value configuration value as string
     * @return converted configuration value
     * @throws IllegalParameterFormatException if the given value could not be converted
     */
    public abstract T convertValue(String groupName, String paramName, String value)
            throws IllegalParameterFormatException;
}
