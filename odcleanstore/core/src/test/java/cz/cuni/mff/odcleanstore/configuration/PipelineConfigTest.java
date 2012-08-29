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
public class PipelineConfigTest {
    private static final String GROUP_NAME = "pipeline";

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(GROUP_NAME + ".look_for_graph_interval")).thenReturn("8000");
        Mockito.when(properties.getProperty(GROUP_NAME + ".second_crash_penalty")).thenReturn("60000");

        PipelineConfig piConfig = PipelineConfig.load(properties);

        assertEquals(new Long(8000), piConfig.getLookForGraphInterval());
        assertEquals(new Long(60000), piConfig.getSecondCrashPenalty());
    }

    @Test(expected = ParameterNotAvailableException.class)
    public void testInvalidGroupName() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);
       
        Mockito.when(properties.getProperty("ipeline.look_for_graph_interval")).thenReturn("8000");
        Mockito.when(properties.getProperty("ipeline.second_crash_penalty")).thenReturn("60000");

        PipelineConfig.load(properties);
    }
}
