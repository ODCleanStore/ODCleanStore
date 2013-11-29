package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

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
public class OutputWSConfigTest {
    private static final Integer PORT = 8087;

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(
                OutputWSConfig.GROUP_PREFIX + "result_data_prefix")).thenReturn("http://odcs.mff.cuni.cz/query/results/");
        Mockito.when(properties.getProperty(
                OutputWSConfig.GROUP_PREFIX + "port")).thenReturn(PORT.toString());
        Mockito.when(properties.getProperty(
                OutputWSConfig.GROUP_PREFIX + "keyword_path")).thenReturn("keyword");
        Mockito.when(properties.getProperty(
                OutputWSConfig.GROUP_PREFIX + "uri_path")).thenReturn("uri");
        Mockito.when(properties.getProperty(
                OutputWSConfig.GROUP_PREFIX + "metadata_path")).thenReturn("metadata");
        Mockito.when(properties.getProperty(
                OutputWSConfig.GROUP_PREFIX + "named_graph_path")).thenReturn("namedGraph");

        OutputWSConfig outputWSConfig = OutputWSConfig.load(properties);

        assertEquals(new URI("http://odcs.mff.cuni.cz/query/results/"), outputWSConfig.getResultDataURIPrefix());

        assertEquals(PORT, outputWSConfig.getPort());
        assertEquals("keyword", outputWSConfig.getKeywordPath());
        assertEquals("uri", outputWSConfig.getUriPath());
        assertEquals("metadata", outputWSConfig.getMetadataPath());
        assertEquals("namedGraph", outputWSConfig.getNamedGraphPath());
    }
}
