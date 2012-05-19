package cz.cuni.mff.odcleanstore.configuration;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatLong;
import cz.cuni.mff.odcleanstore.configuration.formats.FormatURI;
import cz.cuni.mff.odcleanstore.configuration.formats.ParameterFormat;

import java.net.URI;
import java.util.Properties;

/**
 * Encapsulates Query-Execution configuration.
 *
 * It is intended to be used in the following way:
 *
 * <ul>
 * <li>extract Query-Execution configuration values from a Properties instance via {@link #load}, and then</li>
 * <li>query for particular configuration values using the appropriate getter methods.</li>
 * </ul>
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class QueryExecutionConfig extends ConfigGroup {
    static
    {
        GROUP_NAME = "query_execution";
    }

    private Long maxQueryResultSize;
    private URI resultGraphURIPrefix;

    /**
     *
     * @param maxQueryResultSize
     * @param resultGraphURIPrefix
     */
    public QueryExecutionConfig(Long maxQueryResultSize, URI resultGraphURIPrefix) {
        this.maxQueryResultSize = maxQueryResultSize;
        this.resultGraphURIPrefix = resultGraphURIPrefix;
    }

    /**
     * Extracts Query-Execution configuration values from the given Properties instance.
     * Returns a QueryExecutionConfig object instantiated using the extracted values.
     *
     * @param properties
     * @return
     * @throws ParameterNotAvailableException
     * @throws IllegalParameterFormatException
     */
    public static QueryExecutionConfig load(Properties properties)
            throws ParameterNotAvailableException, IllegalParameterFormatException {
        ParameterFormat<Long> formatLong = new FormatLong();
        Long maxQueryResultSize = loadParam(properties, "max_query_result_size", formatLong);

        ParameterFormat<URI> formatURI = new FormatURI();
        URI resultGraphURIPrefix = loadParam(properties, "result_graph_uri_prefix", formatURI);

        return new QueryExecutionConfig(
                maxQueryResultSize,
                resultGraphURIPrefix);
    }

    /**
     *
     * @return
     */
    public Long getMaxQueryResultSize() {
        return maxQueryResultSize;
    }

    /**
     *
     * @return
     */
    public URI getResultGraphURIPrefix() {
        return resultGraphURIPrefix;
    }
}
