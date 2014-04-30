package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.ConfigGroup.EnumDbConnectionType;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class BackendConfigTest extends ConfigTestBase {
    private static final String GROUP_NAME = "backend";
    
    private static final Integer TIMEOUT = 30;

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        mockSparqlEndpointsConnectionCredentials(properties, EnumDbConnectionType.CLEAN, false);
        mockSparqlEndpointsConnectionCredentials(properties, EnumDbConnectionType.DIRTY, false);
        mockJDBCConnectionCredentials(properties, EnumDbConnectionType.CLEAN);
        mockJDBCConnectionCredentials(properties, EnumDbConnectionType.DIRTY);
        mockQueryTimeout(properties);
        

        BackendConfig backendConfig = BackendConfig.load(properties);

        checkSparqlEndpointsConnectionCredentials(
                backendConfig.getCleanDBSparqlConnectionCredentials(), EnumDbConnectionType.CLEAN, false);
        checkSparqlEndpointsConnectionCredentials(
                backendConfig.getDirtyDBSparqlConnectionCredentials(), EnumDbConnectionType.DIRTY, false);
        checkJDBCConnectionCredentials(
                backendConfig.getCleanDBJDBCConnectionCredentials(), EnumDbConnectionType.CLEAN);
        checkJDBCConnectionCredentials(
                backendConfig.getDirtyDBJDBCConnectionCredentials(), EnumDbConnectionType.DIRTY);
        
        checkQueryTimeout(backendConfig);
    }

    private void mockQueryTimeout(Properties properties) {
        Mockito.when(properties.getProperty(GROUP_NAME + ".query_timeout")).thenReturn(TIMEOUT.toString());
    }

    private void checkQueryTimeout(BackendConfig config) {
        assertEquals(TIMEOUT, config.getQueryTimeout());
    }



}