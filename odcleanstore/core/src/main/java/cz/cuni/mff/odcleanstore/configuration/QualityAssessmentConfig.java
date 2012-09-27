package cz.cuni.mff.odcleanstore.configuration;

import java.net.URI;
import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

/**
* Encapsulates Quality Assessment transformer configuration.
* @author Jakub Daniel
*/
public class QualityAssessmentConfig extends ConfigGroup {
    /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "quality_assessment" + NAME_DELIMITER;

    private URI aggregatedPublisherScoreGraphURI;
    private URI temporaryGraphURIPrefix;

    public QualityAssessmentConfig(URI aggregatedPublisherScoreGraphURI, URI temporaryGraphURIPrefix) {
        this.aggregatedPublisherScoreGraphURI = aggregatedPublisherScoreGraphURI;
        this.temporaryGraphURIPrefix = temporaryGraphURIPrefix;
    }

    public static QualityAssessmentConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        URI aggregatedPublisherScoreGraphURI = loadParam(
                properties, GROUP_PREFIX + "aggregated_publisher_score_graph_uri", formatURI);
        URI temporaryGraphURIPrefix = loadParam(properties, WebFrontendConfig.GROUP_PREFIX + "debug_temp_graph_uri_prefix", formatURI);

        return new QualityAssessmentConfig(aggregatedPublisherScoreGraphURI, temporaryGraphURIPrefix);
    }

    public URI getAggregatedPublisherScoreGraphURI() {
        return aggregatedPublisherScoreGraphURI;
    }
    
    public URI getTemporaryGraphURIPrefix() {
        return temporaryGraphURIPrefix;
    }
}
