package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

import java.net.URI;
import java.util.Properties;

/**
 * Encapsulates Object-Identification configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Object-Identification configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ObjectIdentificationConfig extends ConfigGroup {
    static
    {
        GROUP_NAME = "object_identification";
    }

    private URI linksGraphURIPrefix;

    /**
     *
     * @param linksGraphUriPrefix
     */
    public ObjectIdentificationConfig(URI linksGraphURIPrefix) {
        this.linksGraphURIPrefix = linksGraphURIPrefix;
    }

    /**
     * Extracts Object-Identification configuration values from the given Properties instance.
     * Returns a ObjectIdentificationConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static ObjectIdentificationConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        URI linksGraphURIPrefix = loadParam(properties, "links_graph_uri_prefix", formatURI);

        return new ObjectIdentificationConfig(linksGraphURIPrefix);
    }

    /**
     *
     * @return
     */
    public URI getLinksGraphURIPrefix() {
        return linksGraphURIPrefix;
    }
}
