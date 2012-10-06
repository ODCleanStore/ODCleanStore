package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.ConfigGroup.EnumDbConnectionType;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;

import org.junit.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ObjectIdentificationConfigTest extends ConfigTestBase {

    @Test
    public void testCorrectConfiguration() throws ConfigurationException, URISyntaxException, MalformedURLException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(ObjectIdentificationConfig.GROUP_PREFIX + "link_within_graph")).thenReturn("false");
        mockSparqlEndpointsConnectionCredentials(properties, EnumDbConnectionType.CLEAN, false);
        mockSparqlEndpointsConnectionCredentials(properties, EnumDbConnectionType.DIRTY_UPDATE, true);
        
        ObjectIdentificationConfig oiConfig = ObjectIdentificationConfig.load(properties);

        checkSparqlEndpointsConnectionCredentials(
                oiConfig.getCleanDBSparqlConnectionCredentials(), EnumDbConnectionType.CLEAN, false);
        checkSparqlEndpointsConnectionCredentials(
                oiConfig.getDirtyDBSparqlConnectionCredentials(), EnumDbConnectionType.DIRTY_UPDATE, true);
    }
}
