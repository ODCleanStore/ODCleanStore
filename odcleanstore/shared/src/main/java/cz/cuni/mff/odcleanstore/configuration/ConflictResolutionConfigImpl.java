package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatDouble;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatLong;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

import java.util.Properties;

/**
 * Encapsulates Conflict-Resolution configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Conflict-Resolution configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ConflictResolutionConfigImpl extends ConfigGroup implements ConflictResolutionConfig {
    /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "conflict_resolution" + NAME_DELIMITER;

    private final Double agreeCoeficient;
    private final Double scoreIfUnknown;
    private final Double namedGraphScoreWeight;
    private final Double publisherScoreWeight;
    private final Long maxDateDifference;

    /**
     * @param agreeCoeficient
     * @param scoreIfUnknown
     * @param namedGraphScoreWeight
     * @param publisherScoreWeight
     * @param maxDateDifference
     */
    public ConflictResolutionConfigImpl(Double agreeCoeficient, Double scoreIfUnknown,
            Double namedGraphScoreWeight, Double publisherScoreWeight, Long maxDateDifference)
    {
        this.agreeCoeficient = agreeCoeficient;
        this.scoreIfUnknown = scoreIfUnknown;
        this.namedGraphScoreWeight = namedGraphScoreWeight;
        this.publisherScoreWeight = publisherScoreWeight;
        this.maxDateDifference = maxDateDifference;
    }

    /**
     * Extracts Conflict-Resolution configuration values from the given Properties instance.
     * Returns a ConflictResolutionConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static ConflictResolutionConfigImpl load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<Double> formatDouble = new FormatDouble();

        Double agreeCoeficient = loadParam(properties, GROUP_PREFIX + "agree_coefficient", formatDouble);
        Double scoreIfUnknown = loadParam(properties, GROUP_PREFIX + "score_if_unknown", formatDouble);
        Double namedGraphScoreWeight = loadParam(properties, GROUP_PREFIX + "named_graph_score_weight", formatDouble);
        Double publisherScoreWeight = loadParam(properties, GROUP_PREFIX + "publisher_score_weight", formatDouble);

        ParameterFormat<Long> formatLong = new FormatLong();
        Long maxDateDifference = loadParam(properties, GROUP_PREFIX + "max_date_difference", formatLong);

        return new ConflictResolutionConfigImpl(
                agreeCoeficient,
                scoreIfUnknown,
                namedGraphScoreWeight,
                publisherScoreWeight,
                maxDateDifference);
    }

    @Override
    public Double getAgreeCoeficient() {
        return agreeCoeficient;
    }

    @Override
    public Double getScoreIfUnknown() {
        return scoreIfUnknown;
    }

    @Override
    public Double getNamedGraphScoreWeight() {
        return namedGraphScoreWeight;
    }

    @Override
    public Double getPublisherScoreWeight() {
        return publisherScoreWeight;
    }

    @Override
    public Long getMaxDateDifference() {
        return maxDateDifference;
    }
}
