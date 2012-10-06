package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;

import java.util.Properties;

/**
* Encapsulates Quality Assessment transformer configuration.
* @author Jakub Daniel
*/
public class QualityAssessmentConfig extends ConfigGroup {
    /** Prefix of names of properties belonging to this group. */
    public static final String GROUP_PREFIX = "quality_assessment" + NAME_DELIMITER;

    public QualityAssessmentConfig() {
        
    }

    public static QualityAssessmentConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {

        return new QualityAssessmentConfig();
    }

}
