package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURL;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;
import cz.cuni.mff.odcleanstore.connection.SparqlEndpointCoords;

import java.net.URL;
import java.util.Properties;

/**
 * Encapsulates InputWS configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract InputWS configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class InputWSConfig extends ConfigGroup {
    static
    {
        GROUP_NAME = "input_ws";
    }

    // TODO: doresit jak se konfiguruje instalacni adresar
    private String inputDirPath;
    private SparqlEndpointCoords sparqlEndpointCoords;

    /**
     *
     * @param inputDirPath
     * @param sparqlEndpointCoords
     */
    public InputWSConfig(String inputDirPath, SparqlEndpointCoords sparqlEndpointCoords) {
        this.inputDirPath = inputDirPath;
        this.sparqlEndpointCoords = sparqlEndpointCoords;
    }

    /**
     * Extracts InputWS configuration values from the given Properties instance.
     * Returns a InputWSConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static InputWSConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException
    {
        ParameterFormat<String> formatString = new FormatString();
        String inputDirPath = loadParam(properties, "input_dir_path", formatString);

        ParameterFormat<URL> formatURL = new FormatURL();
        URL endpointURL = loadParam(properties, "endpoint_url", formatURL);

        return new InputWSConfig(
                inputDirPath,
                new SparqlEndpointCoords(endpointURL));
    }

    /**
     *
     * @return
     */
    public String getInputDirPath() {
        return inputDirPath;
    }

    /**
     *
     * @return
     */
    public SparqlEndpointCoords getSparqlEndpointCoords() {
        return sparqlEndpointCoords;
    }
}
