package cz.cuni.mff.odcleanstore.configuration;

import java.net.URI;
import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

/**
* Encapsulates Data Normalization transformer configuration.
* @author Jakub Daniel
*/
public class DataNormalizationConfig extends ConfigGroup {
    //public static final String GROUP_PREFIX = "data_normalization" + NAME_DELIMITER;

    private URI temporaryGraphURIPrefix;

    public DataNormalizationConfig(URI temporaryGraphURIPrefix) {
        this.temporaryGraphURIPrefix = temporaryGraphURIPrefix;
    }

    public static DataNormalizationConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        URI temporaryGraphURIPrefix = loadParam(properties, WebFrontendConfig.GROUP_PREFIX + "debug_temp_graph_uri_prefix", formatURI);

        return new DataNormalizationConfig(temporaryGraphURIPrefix);
    }

    public URI getTemporaryGraphURIPrefix() {
        return temporaryGraphURIPrefix;
    }
}
