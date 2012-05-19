package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class QueryExecutionConfigTest {
    private static final String GROUP_NAME = "query_execution";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(GROUP_NAME + ".max_query_result_size")).thenReturn("500");
        Mockito.when(properties.getProperty(GROUP_NAME + ".result_graph_uri_prefix")).thenReturn(
                "http://odcs.mff.cuni.cz/query/results/");

        QueryExecutionConfig qeConfig = QueryExecutionConfig.load(properties);

        assertEquals(new Long(500), qeConfig.getMaxQueryResultSize());
        assertEquals(new URI("http://odcs.mff.cuni.cz/query/results/"), qeConfig.getResultGraphURIPrefix());
    }

    @Test(expected = ParameterNotAvailableException.class)
    public void testInvalidGroupName() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty("qery_execution.max_query_result_size")).thenReturn("500");
        Mockito.when(properties.getProperty("qery_execution.result_graph_uri_prefix")).thenReturn(
                "http://odcs.mff.cuni.cz/query/results/");

        QueryExecutionConfig.load(properties);
    }
}
