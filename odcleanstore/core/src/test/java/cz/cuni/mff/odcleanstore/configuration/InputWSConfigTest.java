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
public class InputWSConfigTest {
    private static final String GROUP_NAME = "input_ws";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(GROUP_NAME + ".input_dir_path")).thenReturn("inputWS");
        Mockito.when(properties.getProperty(GROUP_NAME + ".endpoint_url")).thenReturn("http://localhost:8088/inputws");
        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_jdbc_connection_string")).thenReturn("jdbc:virtuoso://localhost:1113");
        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_jdbc_username")).thenReturn("dba");
        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_jdbc_password")).thenReturn("dba");
        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_sparql_endpoint_url")).thenReturn("http://localhost:8892/sparql");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_jdbc_connection_string")).thenReturn("jdbc:virtuoso://localhost:1113");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_jdbc_username")).thenReturn("dba");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_jdbc_password")).thenReturn("dba");
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_url")).thenReturn("http://localhost:8892/sparql");
        
        InputWSConfig inputWSConfig = InputWSConfig.load(properties);

        assertEquals("inputWS" + File.separator, inputWSConfig.getInputDirPath());

        assertEquals(new URL("http://localhost:8088/inputws"), inputWSConfig.getSparqlEndpointConnectionCredentials()
                .getUrl());
    }
}
