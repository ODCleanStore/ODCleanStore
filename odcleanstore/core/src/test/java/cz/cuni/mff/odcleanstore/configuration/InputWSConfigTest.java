package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

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

        Mockito.when(properties.getProperty(GROUP_NAME + ".input_dir_path")).thenReturn("engineScraperInput");
        Mockito.when(properties.getProperty(GROUP_NAME + ".endpoint_url")).thenReturn(
                "http://localhost:8088/odcleanstore/scraper");

        InputWSConfig inputWSConfig = InputWSConfig.load(properties);

        assertEquals("engineScraperInput", inputWSConfig.getInputDirPath());

        assertEquals(new URL("http://localhost:8088/odcleanstore/scraper"), inputWSConfig.getSparqlEndpointCoords()
                .getUrl());
    }
}
