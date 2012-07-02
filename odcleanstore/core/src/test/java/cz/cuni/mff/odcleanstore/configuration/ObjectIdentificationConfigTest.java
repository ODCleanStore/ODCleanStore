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
public class ObjectIdentificationConfigTest {
    private static final String GROUP_NAME = "object_identification";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(GROUP_NAME + ".links_graph_uri_prefix")).thenReturn("http://www.seznam.cz");
        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_sparql_endpoint_url")).thenReturn("http://www.google.cz");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_url")).thenReturn("http://www.yahoo.com");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_username")).thenReturn("Pepa");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_password")).thenReturn("heslo");

        ObjectIdentificationConfig oiConfig = ObjectIdentificationConfig.load(properties);

        assertEquals(new URI("http://www.seznam.cz"), oiConfig.getLinksGraphURIPrefix());
    }
}
