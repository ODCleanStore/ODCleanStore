package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;

import java.util.Properties;

/**
* Encapsulates Data Normalization transformer configuration.
* @author Jakub Daniel
*/
public class DataNormalizationConfig extends ConfigGroup {
    //public static final String GROUP_PREFIX = "data_normalization" + NAME_DELIMITER;


    public DataNormalizationConfig() {
    }

    public static DataNormalizationConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        
        return new DataNormalizationConfig();
    }

}
