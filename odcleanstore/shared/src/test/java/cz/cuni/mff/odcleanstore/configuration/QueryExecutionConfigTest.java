package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.ConfigGroup.EnumDbConnectionType;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class QueryExecutionConfigTest extends ConfigTestBase {
    private static final Long RESULT_SIZE = 500L;

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(
                QueryExecutionConfig.GROUP_PREFIX + "max_query_result_size")).thenReturn(RESULT_SIZE.toString());
        Mockito.when(properties.getProperty(
                OutputWSConfig.GROUP_PREFIX + "result_data_prefix"))
                .thenReturn("http://odcs.mff.cuni.cz/query/results/");
        mockJDBCConnectionCredentials(properties, EnumDbConnectionType.CLEAN);

        QueryExecutionConfig qeConfig = QueryExecutionConfig.load(properties);

        assertEquals(RESULT_SIZE, qeConfig.getMaxQueryResultSize());
        assertEquals(new URI("http://odcs.mff.cuni.cz/query/results/"), qeConfig.getResultDataURIPrefix());
        checkJDBCConnectionCredentials(
                qeConfig.getCleanDBJDBCConnectionCredentials(), EnumDbConnectionType.CLEAN);
    }

    @Test(expected = ParameterNotAvailableException.class)
    public void testInvalidGroupName() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty("qery_execution.max_query_result_size")).thenReturn(RESULT_SIZE.toString());
        Mockito.when(properties.getProperty("qery_execution.result_data_prefix")).thenReturn(
                "http://odcs.mff.cuni.cz/query/results/");

        QueryExecutionConfig.load(properties);
    }
}
