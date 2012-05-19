package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.connection.JDBCCoords;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class BackendConfigTest {
    private static final String GROUP_NAME = "backend";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        mockDirtyJDBCCoords(properties);
        mockCleanJDBCCoords(properties);
        mockSparqlEndpointsCoords(properties);
        mockQueryTimeout(properties);
        mockGraphUriPrefixes(properties);

        BackendConfig backendConfig = BackendConfig.load(properties);

        checkDirtyJDBCCoords(backendConfig);
        checkCleanJDBCCoords(backendConfig);
        checkSparqlEndpointsCoords(backendConfig);
        checkQueryTimeout(backendConfig);
        checkGraphUriPrefixes(backendConfig);
    }

    private void mockQueryTimeout(Properties properties) {
        Mockito.when(properties.getProperty(GROUP_NAME + ".query_timeout")).thenReturn("30");
    }

    private void checkQueryTimeout(BackendConfig config) {
        assertEquals(new Integer(30), config.getQueryTimeout());
    }

    private void mockGraphUriPrefixes(Properties properties) {
        Mockito.when(properties.getProperty(GROUP_NAME + ".data_graph_uri_prefix")).thenReturn(
                "http://opendata.cz/infrastructure/odcleanstore/");

        Mockito.when(properties.getProperty(GROUP_NAME + ".metadata_graph_uri_prefix")).thenReturn(
                "http://opendata.cz/infrastructure/odcleanstore/metadata/");
    }

    private void checkGraphUriPrefixes(BackendConfig config) throws URISyntaxException {
        assertEquals(new URI("http://opendata.cz/infrastructure/odcleanstore/"), config.getDataGraphURIPrefix());

        assertEquals(new URI("http://opendata.cz/infrastructure/odcleanstore/metadata/"),
                config.getMetadataGraphURIPrefix());
    }

    private void mockSparqlEndpointsCoords(Properties properties) {
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_sparql_endpoint_url")).thenReturn(
                "http://www.sparql.cz/dirty");

        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_sparql_endpoint_url")).thenReturn(
                "http://www.sparql.cz/clean");
    }

    private void checkSparqlEndpointsCoords(BackendConfig config) throws MalformedURLException {
        assertEquals(new URL("http://www.sparql.cz/dirty"), config.getDirtyDBSparqlCoords().getUrl());

        assertEquals(new URL("http://www.sparql.cz/clean"), config.getCleanDBSparqlCoords().getUrl());
    }

    private void mockDirtyJDBCCoords(Properties properties) {
        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_jdbc_url")).thenReturn("http://www.jdbc.cz/dirty");

        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_jdbc_username")).thenReturn("dba");

        Mockito.when(properties.getProperty(GROUP_NAME + ".dirty_jdbc_password")).thenReturn("dba");
    }

    private void checkDirtyJDBCCoords(BackendConfig config) throws MalformedURLException {
        JDBCCoords dirtyJDBCCoords = config.getDirtyDBJDBCCoords();

        assertEquals(new URL("http://www.jdbc.cz/dirty"), dirtyJDBCCoords.getUrl());

        assertEquals("dba", dirtyJDBCCoords.getUsername());

        assertEquals("dba", dirtyJDBCCoords.getPassword());
    }

    private void mockCleanJDBCCoords(Properties properties) {
        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_jdbc_url")).thenReturn("http://www.jdbc.cz/clean");

        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_jdbc_username")).thenReturn("dba");

        Mockito.when(properties.getProperty(GROUP_NAME + ".clean_jdbc_password")).thenReturn("dba");
    }

    private void checkCleanJDBCCoords(BackendConfig config) throws MalformedURLException {
        JDBCCoords cleanJDBCCoords = config.getCleanDBJDBCCoords();

        assertEquals(new URL("http://www.jdbc.cz/clean"), cleanJDBCCoords.getUrl());

        assertEquals("dba", cleanJDBCCoords.getUsername());

        assertEquals("dba", cleanJDBCCoords.getPassword());
    }
}
