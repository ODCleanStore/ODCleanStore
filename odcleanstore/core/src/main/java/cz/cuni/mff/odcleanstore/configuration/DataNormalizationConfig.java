package cz.cuni.mff.odcleanstore.configuration;

import java.net.URI;
import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

public class DataNormalizationConfig extends ConfigGroup {
	static
    {
        GROUP_NAME = "data_normalization";
    }

    private URI temporaryGraphURIPrefix;

    public DataNormalizationConfig(URI temporaryGraphURIPrefix) {
        this.temporaryGraphURIPrefix = temporaryGraphURIPrefix;
    }

    public static DataNormalizationConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        URI temporaryGraphURIPrefix = loadParam(properties, "temporary_graph_uri_prefix", formatURI);

        return new DataNormalizationConfig(temporaryGraphURIPrefix);
    }

    public URI getTemporaryGraphURIPrefix() {
        return temporaryGraphURIPrefix;
    }
}
