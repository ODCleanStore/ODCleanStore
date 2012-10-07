package cz.cuni.mff.odcleanstore.configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

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
    private DataNormalizationConfig dataNormalizationGroup;
    private QualityAssessmentConfig qualityAssessmentGroup;
    private ObjectIdentificationConfig objectIdentificationGroup;
    private QueryExecutionConfig queryExecutionGroup;
    private ConflictResolutionConfig conflictResolutionGroup;
    private OutputWSConfig outputWSGroup;
    private InputWSConfig inputWSGroup;
    private EngineConfig engineGroup;
    private WebFrontendConfig webFrontendGroup;

    /**
     * Create a new instance with the given configuration values.
     * @param backendConfigGroup backend configuration
     * @param queryExecutionGroup Query Execution configuration
     * @param conflictResolutionGroup ConflictResolution configuration
     * @param objectIdentificationGroup Object Identification configuration
     * @param outputWSGroup input webservice configuration
     * @param inputWSGroup output webservice configuration
     * @param engineGroup engine configuration
     * @param webFrontendGroup web frontend configuration
     */
    // CHECKSTYLE:OFF
    private Config(BackendConfig backendConfigGroup, DataNormalizationConfig dataNormalizationGroup,
            QualityAssessmentConfig qualityAssessmentGroup, ObjectIdentificationConfig objectIdentificationGroup,
            QueryExecutionConfig queryExecutionGroup, ConflictResolutionConfig conflictResolutionGroup,
            OutputWSConfig outputWSGroup, InputWSConfig inputWSGroup, EngineConfig engineGroup,
            WebFrontendConfig webFrontendGroup) {
        this.backendConfigGroup = backendConfigGroup;
        this.dataNormalizationGroup = dataNormalizationGroup;
        this.qualityAssessmentGroup = qualityAssessmentGroup;
        this.objectIdentificationGroup = objectIdentificationGroup;
        this.queryExecutionGroup = queryExecutionGroup;
        this.conflictResolutionGroup = conflictResolutionGroup;
        this.outputWSGroup = outputWSGroup;
        this.inputWSGroup = inputWSGroup;
        this.engineGroup = engineGroup;
        this.webFrontendGroup = webFrontendGroup;
    }
    // CHECKSTYLE:ON

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
        DataNormalizationConfig dataNormalizationGroup = DataNormalizationConfig.load(properties);
        QualityAssessmentConfig qualityAssessmentGroup = QualityAssessmentConfig.load(properties);
        ObjectIdentificationConfig objectIdentificationGroup = ObjectIdentificationConfig.load(properties);
        QueryExecutionConfig queryExecutionGroup = QueryExecutionConfig.load(properties);
        ConflictResolutionConfig conflictResolutionGroup = ConflictResolutionConfig.load(properties);
        OutputWSConfig outputWSGroup = OutputWSConfig.load(properties);
        InputWSConfig inputWSGroup = InputWSConfig.load(properties);
        EngineConfig engineConfig = EngineConfig.load(properties);
        WebFrontendConfig webFrontendConfig = WebFrontendConfig.load(properties);

        return new Config(backendConfigGroup, dataNormalizationGroup, qualityAssessmentGroup, objectIdentificationGroup,
                queryExecutionGroup, conflictResolutionGroup, outputWSGroup, inputWSGroup, engineConfig,
                webFrontendConfig);
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
     * Returns DN configuration.
     * @return DN configuration
     */
    public DataNormalizationConfig getDataNormalizationGroup() {
        return dataNormalizationGroup;
    }

    /**
     * Returns QA configuration.
     * @return QA configuration
     */
    public QualityAssessmentConfig getQualityAssessmentGroup() {
        return qualityAssessmentGroup;
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
    
    /**
     * Returns engine configuration.
     * @return engine configuration
     */
    public EngineConfig getEngineGroup() {
        return engineGroup;
    }
    
    /**
     * Returns web frontend configuration.
     * @return web frontend configuration
     */
    public WebFrontendConfig getWebFrontendGroup() {
    	return webFrontendGroup;
    }
}
