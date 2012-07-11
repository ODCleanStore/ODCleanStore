package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Encapsulates application configuration values.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract configuration values from a File via {@link #load(File)}, and then</li>
 * <li>obtain the appropriate configuration group using the corresponding getter method and query it for particular
 * configuration values.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
// TODO: doplnit integracni testy pro parse cele konfigurace ze souboru
public final class Config {
    private BackendConfig backendConfigGroup;
    private ObjectIdentificationConfig objectIdentificationGroup;
    private QueryExecutionConfig queryExecutionGroup;
    private ConflictResolutionConfig conflictResolutionGroup;
    private OutputWSConfig outputWSGroup;
    private InputWSConfig inputWSGroup;

    /**
     * Create a new instance with the given configuration values.
     * @param backendConfigGroup backend configuration
     * @param queryExecutionGroup Query Execution configuration
     * @param conflictResolutionGroup ConflictResolution configuration
     * @param objectIdentificationGroup Object Identification configuration
     * @param outputWSGroup input webservice configuration
     * @param inputWSGroup output webservice configuration
     */
    private Config(BackendConfig backendConfigGroup, ObjectIdentificationConfig objectIdentificationGroup,
            QueryExecutionConfig queryExecutionGroup, ConflictResolutionConfig conflictResolutionGroup,
            OutputWSConfig outputWSGroup, InputWSConfig inputWSGroup) {
        this.backendConfigGroup = backendConfigGroup;
        this.objectIdentificationGroup = objectIdentificationGroup;
        this.queryExecutionGroup = queryExecutionGroup;
        this.conflictResolutionGroup = conflictResolutionGroup;
        this.outputWSGroup = outputWSGroup;
        this.inputWSGroup = inputWSGroup;
    }

    /**
     * Extracts configuration values from the given Properties instance and returns them
     * encapsulated in a Config instance.
     *
     * @param properties unparsed configuration values
     * @return configuration holder instance
     * @throws ConfigurationException exception
     */
    public static Config load(Properties properties) throws ConfigurationException {
        BackendConfig backendConfigGroup = BackendConfig.load(properties);
        ObjectIdentificationConfig objectIdentificationGroup = ObjectIdentificationConfig.load(properties);
        QueryExecutionConfig queryExecutionGroup = QueryExecutionConfig.load(properties);
        ConflictResolutionConfig conflictResolutionGroup = ConflictResolutionConfig.load(properties);
        OutputWSConfig outputWSGroup = OutputWSConfig.load(properties);
        InputWSConfig inputWSGroup = InputWSConfig.load(properties);

        return new Config(backendConfigGroup, objectIdentificationGroup, queryExecutionGroup, conflictResolutionGroup,
                outputWSGroup, inputWSGroup);
    }

    /**
     * Extracts configuration values from the given file and returns them encapsulated
     * in a Config instance.
     *
     * The format of the given file is supposed to adhere to the Java Properties rules
     * (see <a href="http://docs.oracle.com/javase/1.4.2/docs/api/java/util/Properties.html">
     * Java SE javadoc</a> for more information on properties).
     *
     * @param file file to load configuration from
     * @return configuration holder instance
     * @throws ConfigurationException exception
     */
    public static Config load(File file) throws ConfigurationException {
        Properties properties = new Properties();

        try {
            properties.load(new FileReader(file));
        } catch (IOException ex) {
            throw new ConfigurationException("Unavailable configuration file: " + file.getAbsolutePath());
        }

        return load(properties);
    }

    /**
     * Returns backend configuration.
     * @return backend configuration
     */
    public BackendConfig getBackendGroup() {
        return backendConfigGroup;
    }

    /**
     * Returns OI configuration.
     * @return OI configuration
     */
    public ObjectIdentificationConfig getObjectIdentificationConfig() {
        return objectIdentificationGroup;
    }

    /**
     * Returns QE configuration.
     * @return QE configuration
     */
    public QueryExecutionConfig getQueryExecutionGroup() {
        return queryExecutionGroup;
    }

    /**
     * Returns CR configuration.
     * @return CR configuration
     */
    public ConflictResolutionConfig getConflictResolutionGroup() {
        return conflictResolutionGroup;
    }

    /**
     * Returns output webservice configuration.
     * @return output webservice configuration
     */
    public OutputWSConfig getOutputWSGroup() {
        return outputWSGroup;
    }

    /**
     * Returns input webservice configuration.
     * @return input webservice configuration
     */
    public InputWSConfig getInputWSGroup() {
        return inputWSGroup;
    }
}
