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
    private static final String GROUP_NAME = "output_ws";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(GROUP_NAME + ".metadata_graph_uri")).thenReturn(
                "http://odcs.mff.cuni.cz/query/metadata/");
        Mockito.when(properties.getProperty(GROUP_NAME + ".port")).thenReturn("8087");
        Mockito.when(properties.getProperty(GROUP_NAME + ".keyword_path")).thenReturn("keyword");
        Mockito.when(properties.getProperty(GROUP_NAME + ".uri_path")).thenReturn("uri");

        OutputWSConfig outputWSConfig = OutputWSConfig.load(properties);

        assertEquals(new URI("http://odcs.mff.cuni.cz/query/metadata/"), outputWSConfig.getMetadataGraphURIPrefix());

        assertEquals(new Integer(8087), outputWSConfig.getPort());
        assertEquals("keyword", outputWSConfig.getKeywordPath());
        assertEquals("uri", outputWSConfig.getUriPath());
    }
}
