package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class InputWSConfigTest extends ConfigTestBase {
    @Test
    public void testCorrectConfiguration() throws ConfigurationException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(
                InputWSConfig.GROUP_PREFIX + "endpoint_url")).thenReturn("http://localhost:8088/inputws");
        Mockito.when(properties.getProperty(
                InputWSConfig.GROUP_PREFIX + "named_graphs_prefix")).thenReturn("http://opendata.cz/infrastructure/odcleanstore/");
        Mockito.when(properties.getProperty(InputWSConfig.GROUP_PREFIX + "recovery_crash_penalty")).thenReturn("60000");
        
        InputWSConfig inputWSConfig = InputWSConfig.load(properties);

        assertEquals(new URL("http://localhost:8088/inputws"), inputWSConfig.getEndpointURL());
        assertEquals(new URL("http://opendata.cz/infrastructure/odcleanstore/"), inputWSConfig.getNamedGraphsPrefix());
        assertEquals(new Long(60000), inputWSConfig.getRecoveryCrashPenalty());
    }
}
