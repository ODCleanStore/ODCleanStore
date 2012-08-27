package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatInteger;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatString;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

import java.net.URI;
import java.util.Properties;

/**
 * Encapsulates OutputWS configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract OutputWS configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class OutputWSConfig extends ConfigGroup {
    static
    {
        GROUP_NAME = "output_ws";
    }

    private URI metadataGraphURIPrefix;
    private String qaRuleURIPrefix;
    private Integer port;
    private String keywordPath;
    private String uriPath;
    private String metadataPath;
    private String namedGraphPath;

    /**
     *
     * @param metadataGraphURIPrefix URI of named graph where metadata in the output are stored
     * @param qaRuleURIPrefix Prefix or URIs identifying a Quality Assessment rule
     * @param port Port of the output webservice
     * @param keywordPath Relative path fragment for the keyword query over the output webservice
     * @param uriPath Relative path fragment for the uri query over the output webservice
     * @param metadataPath Relative path fragment for the metadata query over the output webservice
     * @param namedGraphPath Relative path fragment for the named graph query over the output webservice
     */
    public OutputWSConfig(URI metadataGraphURIPrefix, String qaRuleURIPrefix, Integer port,
            String keywordPath, String uriPath, String metadataPath, String namedGraphPath) {
        this.metadataGraphURIPrefix = metadataGraphURIPrefix;
        this.qaRuleURIPrefix = qaRuleURIPrefix;
        this.port = port;
        this.keywordPath = keywordPath;
        this.uriPath = uriPath;
        this.metadataPath = metadataPath;
        this.namedGraphPath = namedGraphPath;
    }

    /**
     * Extracts OutputWS configuration values from the given Properties instance.
     * Returns a OutputWSConfig object instantiated using the extracted values.
     *
     * @param properties configuration properties
     * @return OutputWS configuration holder
     * @throws ParameterNotAvailableException exception
     * @throws IllegalParameterFormatException exception
     */
    public static OutputWSConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<URI> formatURI = new FormatURI();
        ParameterFormat<String> formatString = new FormatString();
        URI metadataGraphURIPrefix = loadParam(properties, "metadata_graph_uri", formatURI);
        String qaRuleURIPrefix = loadParam(properties, "qa_rule_uri_prefix", formatString);

        ParameterFormat<Integer> formatInteger = new FormatInteger();
        Integer port = loadParam(properties, "port", formatInteger);

        String keywordPath = loadParam(properties, "keyword_path", formatString);
        String uriPath = loadParam(properties, "uri_path", formatString);
        String metadataPath = loadParam(properties, "metadata_path", formatString);
        String namedGraphPath = loadParam(properties, "named_graph_path", formatString);

        return new OutputWSConfig(
                metadataGraphURIPrefix,
                qaRuleURIPrefix,
                port,
                keywordPath,
                uriPath,
                metadataPath,
                namedGraphPath);
    }

    /**
     *
     * @return URI of named graph where metadata in the output are stored
     */
    public URI getMetadataGraphURIPrefix() {
        return metadataGraphURIPrefix;
    }

    /**
    *
    * @return Prefix or URIs identifying a Quality Assessment rule
    */
   public String getQARuleURIPrefix() {
       return qaRuleURIPrefix;
   }

    
    /**
     *
     * @return Port of the output webservice
     */
    public Integer getPort() {
        return port;
    }

    /**
     *
     * @return Relative path fragment for the keyword query over the output webservice
     */
    public String getKeywordPath() {
        return keywordPath;
    }

    /**
     *
     * @return Relative path fragment for the uri query over the output webservice
     */
    public String getUriPath() {
        return uriPath;
    }
    
    /**
    *
    * @return Relative path fragment for the metadata query over the output webservice
    */
   public String getMetadataPath() {
       return metadataPath;
   }
    
    /**
    *
    * @return Relative path fragment for the named graph query over the output webservice
    */
   public String getNamedGraphPath() {
       return namedGraphPath;
   }
}
