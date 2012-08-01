package cz.cuni.mff.odcleanstore.configuration;

import java.net.URI;
import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

public class QualityAssessmentConfig extends ConfigGroup {
	static
    {
        GROUP_NAME = "quality_assessment";
    }

    private URI aggregatedPublisherScoreGraphURI;

    public QualityAssessmentConfig(URI aggregatedPublisherScoreGraphURI) {
        this.aggregatedPublisherScoreGraphURI = aggregatedPublisherScoreGraphURI;
    }

    public static QualityAssessmentConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        URI aggregatedPublisherScoreGraphURI = loadParam(properties, "aggregated_publisher_score_graph_uri", formatURI);

        return new QualityAssessmentConfig(aggregatedPublisherScoreGraphURI);
    }

    public URI getAggregatedPublisherScoreGraphURI() {
        return aggregatedPublisherScoreGraphURI;
    }
}
