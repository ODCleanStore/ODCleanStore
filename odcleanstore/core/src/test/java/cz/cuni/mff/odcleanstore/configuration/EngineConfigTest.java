package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

import cz.cuni.mff.odcleanstore.configuration.ConfigGroup.EnumDbConnectionType;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;

/**
 *
 * @author Petr Jerman
 *
 */
public class EngineConfigTest extends ConfigTestBase {
    private static final String GROUP_NAME = "engine";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "startup_timeout")).thenReturn("30000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "shutdown_timeout")).thenReturn("40000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "look_for_graph_interval")).thenReturn("50000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "second_crash_penalty")).thenReturn("60000");
        
        mockGraphUriPrefixes(properties);
        mockJDBCConnectionCredentials(properties, EnumDbConnectionType.CLEAN);
        mockJDBCConnectionCredentials(properties, EnumDbConnectionType.DIRTY);

        EngineConfig enConfig = EngineConfig.load(properties);

        assertEquals(new Long(30000), enConfig.getStartupTimeout());
        assertEquals(new Long(40000), enConfig.getShutdownTimeout());
        assertEquals(new Long(50000), enConfig.getLookForGraphInterval());
        assertEquals(new Long(60000), enConfig.getSecondCrashPenalty());
        checkGraphUriPrefixes(enConfig);
        checkJDBCConnectionCredentials(
                enConfig.getCleanDBJDBCConnectionCredentials(), EnumDbConnectionType.CLEAN);
        checkJDBCConnectionCredentials(
                enConfig.getDirtyDBJDBCConnectionCredentials(), EnumDbConnectionType.DIRTY);

    }
    
    @Test(expected = ParameterNotAvailableException.class)
    public void testInvalidGroupName() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);
       
        Mockito.when(properties.getProperty("ngine.startup_timeout")).thenReturn("30000");
        Mockito.when(properties.getProperty("ngine.shutdown_timeout")).thenReturn("40000");
        Mockito.when(properties.getProperty("ngine.look_for_graph_interval")).thenReturn("50000");
        Mockito.when(properties.getProperty("ngine.second_crash_penalty")).thenReturn("60000");

        EngineConfig.load(properties);
    }
    

    private void mockGraphUriPrefixes(Properties properties) {
        Mockito.when(properties.getProperty(GROUP_NAME + ".data_graph_uri_prefix")).thenReturn(
                "http://opendata.cz/infrastructure/odcleanstore/");

        Mockito.when(properties.getProperty(GROUP_NAME + ".metadata_graph_uri_prefix")).thenReturn(
                "http://opendata.cz/infrastructure/odcleanstore/metadata/");
        
        Mockito.when(properties.getProperty(GROUP_NAME + ".provenance_metadata_graph_uri_prefix")).thenReturn(
                "http://opendata.cz/infrastructure/odcleanstore/provenanceMetadata/");
    }
    
    private void checkGraphUriPrefixes(EngineConfig config) throws URISyntaxException {
        assertEquals(new URI("http://opendata.cz/infrastructure/odcleanstore/"), config.getDataGraphURIPrefix());

        assertEquals(new URI("http://opendata.cz/infrastructure/odcleanstore/metadata/"),
                config.getMetadataGraphURIPrefix());
        
        assertEquals(new URI("http://opendata.cz/infrastructure/odcleanstore/provenanceMetadata/"),
                config.getProvenanceMetadataGraphURIPrefix());
    }
}
