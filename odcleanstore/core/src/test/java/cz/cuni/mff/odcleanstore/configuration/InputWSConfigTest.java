package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

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
                InputWSConfig.GROUP_PREFIX + "input_dir_path")).thenReturn("inputWS");
        Mockito.when(properties.getProperty(
                InputWSConfig.GROUP_PREFIX + "endpoint_url")).thenReturn("http://localhost:8088/inputws");
        
        InputWSConfig inputWSConfig = InputWSConfig.load(properties);

        assertEquals("inputWS" + File.separator, inputWSConfig.getInputDirPath());
        assertEquals(new URL("http://localhost:8088/inputws"), inputWSConfig.getEndpointURL());
    }
}
