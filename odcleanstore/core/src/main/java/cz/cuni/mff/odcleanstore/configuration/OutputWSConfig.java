package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatInteger;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

import java.net.URI;
import java.util.Properties;

/**
 * Encapsulates OutputWS configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract OutputWS configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OutputWSConfig extends ConfigGroup {
    static
    {
        GROUP_NAME = "output_ws";
    }

    private URI metadataGraphURIPrefix;
    private Integer port;
    private String keywordPath;
    private String uriPath;

    /**
     *
     * @param metadataGraphURIPrefix
     * @param port
     * @param keywordPath
     * @param uriPath
     */
    public OutputWSConfig(URI metadataGraphURIPrefix, Integer port,
            String keywordPath, String uriPath) {
        this.metadataGraphURIPrefix = metadataGraphURIPrefix;
        this.port = port;
        this.keywordPath = keywordPath;
        this.uriPath = uriPath;
    }

    /**
     * Extracts OutputWS configuration values from the given Properties instance.
     * Returns a OutputWSConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static OutputWSConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        URI metadataGraphURIPrefix = loadParam(properties, "metadata_graph_uri", formatURI);

        ParameterFormat<Integer> formatInteger = new FormatInteger();
        Integer port = loadParam(properties, "port", formatInteger);

        ParameterFormat<String> formatString = new FormatString();
        String keywordPath = loadParam(properties, "keyword_path", formatString);
        String uriPath = loadParam(properties, "uri_path", formatString);

        return new OutputWSConfig(
                metadataGraphURIPrefix,
                port,
                keywordPath,
                uriPath);
    }

    /**
     *
     * @return
     */
    public URI getMetadataGraphURIPrefix() {
        return metadataGraphURIPrefix;
    }

    /**
     *
     * @return
     */
    public Integer getPort() {
        return port;
    }

    /**
     *
     * @return
     */
    public String getKeywordPath() {
        return keywordPath;
    }

    /**
     *
     * @return
     */
    public String getUriPath() {
        return uriPath;
    }
}
