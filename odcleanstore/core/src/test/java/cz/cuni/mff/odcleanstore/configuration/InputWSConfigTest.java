package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class InputWSConfigTest {
    private static final String GROUP_NAME = "input_ws";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(GROUP_NAME + ".input_dir_path")).thenReturn("inputWS");
        Mockito.when(properties.getProperty(GROUP_NAME + ".endpoint_url")).thenReturn("http://localhost:8088/inputws");
        
        InputWSConfig inputWSConfig = InputWSConfig.load(properties);

        assertEquals("inputWS" + File.separator, inputWSConfig.getInputDirPath());
    }
}
