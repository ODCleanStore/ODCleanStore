package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.UUID;

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

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "startup_timeout")).thenReturn("30000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "shutdown_timeout")).thenReturn("40000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "look_for_graph_interval")).thenReturn("50000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "second_crash_penalty")).thenReturn("60000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "state_to_db_writing_interval")).thenReturn("70000");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "dirty_import_export_dir")).thenReturn("dirty/");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "clean_import_export_dir")).thenReturn("clean/");
        Mockito.when(properties.getProperty(EngineConfig.GROUP_PREFIX + "engine_uuid")).thenReturn("88888888-8888-8888-8888-888888888888");
        
        mockJDBCConnectionCredentials(properties, EnumDbConnectionType.CLEAN);
        mockJDBCConnectionCredentials(properties, EnumDbConnectionType.DIRTY);

        EngineConfig enConfig = EngineConfig.load(properties);

        assertEquals(new Long(30000), enConfig.getStartupTimeout());
        assertEquals(new Long(40000), enConfig.getShutdownTimeout());
        assertEquals(new Long(50000), enConfig.getLookForGraphInterval());
        assertEquals(new Long(60000), enConfig.getSecondCrashPenalty());
        assertEquals(new Long(70000), enConfig.getStateToDbWritingInterval());
        assertEquals(new String("dirty/"), enConfig.getDirtyImportExportDir());
        assertEquals(new String("clean/"), enConfig.getCleanImportExportDir());
        assertEquals(UUID.fromString("88888888-8888-8888-8888-888888888888"), enConfig.getEngineUuid());
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
        Mockito.when(properties.getProperty("ngine.state_to_db_writing_interval")).thenReturn("70000");
        Mockito.when(properties.getProperty("ngine.dirty_import_export_dir")).thenReturn("dirty/");
        Mockito.when(properties.getProperty("ngine.clean_import_export_dir")).thenReturn("clean/");
        EngineConfig.load(properties);
    }
    
}
