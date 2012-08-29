package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.Test;
import org.mockito.Mockito;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;

/**
 *
 * @author Petr Jerman
 *
 */
public class EngineConfigTest {
    private static final String GROUP_NAME = "engine";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(GROUP_NAME + ".startup_timeout")).thenReturn("30000");
        Mockito.when(properties.getProperty(GROUP_NAME + ".shutdown_timeout")).thenReturn("30000");

        EngineConfig enConfig = EngineConfig.load(properties);

        assertEquals(new Long(30000), enConfig.getStartupTimeout());
        assertEquals(new Long(30000), enConfig.getShutdownTimeout());
    }

    @Test(expected = ParameterNotAvailableException.class)
    public void testInvalidGroupName() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);
       
        Mockito.when(properties.getProperty("ngine.startup_timeout")).thenReturn("30000");
        Mockito.when(properties.getProperty("ngine.shutdown_timeout")).thenReturn("30000");

        EngineConfig.load(properties);
    }
}
