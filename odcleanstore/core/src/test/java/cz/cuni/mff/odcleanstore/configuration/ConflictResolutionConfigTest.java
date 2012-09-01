package cz.cuni.mff.odcleanstore.configuration;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.ConfigurationException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;
import cz.cuni.mff.odcleanstore.configuration.exceptions.ParameterNotAvailableException;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.Properties;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class ConflictResolutionConfigTest extends ConfigTestBase{
    @Test
    public void testCorrectConfiguration() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "agree_coefficient")).thenReturn("4");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "score_if_unknown")).thenReturn("1");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "named_graph_score_weight")).thenReturn("0.8");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "publisher_score_weight")).thenReturn("0.2");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "max_date_difference")).thenReturn("31622400");

        ConflictResolutionConfig crConfig = ConflictResolutionConfig.load(properties);

        assertEquals(new Double(4.0), crConfig.getAgreeCoeficient());
        assertEquals(new Double(1.0), crConfig.getScoreIfUnknown());
        assertEquals(new Double(0.8), crConfig.getNamedGraphScoreWeight());
        assertEquals(new Double(0.2), crConfig.getPublisherScoreWeight());
        assertEquals(new Long(31622400), crConfig.getMaxDateDifference());
    }

    @Test(expected = ParameterNotAvailableException.class)
    public void testMissingAgreeCoeficient() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "agree_coefficient")).thenReturn(null);
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "score_if_unknown")).thenReturn("1");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "named_graph_score_weight")).thenReturn("0.8");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "publisher_score_weight")).thenReturn("0.2");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "max_date_difference")).thenReturn("31622400");

        ConflictResolutionConfig.load(properties);
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testMalformedAgreeCoeficient() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "agree_coefficient")).thenReturn("4k");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "score_if_unknown")).thenReturn("1");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "named_graph_score_weight")).thenReturn("0.8");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "publisher_score_weight")).thenReturn("0.2");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "max_date_difference")).thenReturn("31622400");

        ConflictResolutionConfig.load(properties);
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testMalformedMaxDateDifference() throws ConfigurationException {
        Properties properties = Mockito.mock(Properties.class);

        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "agree_coefficient")).thenReturn("4");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "score_if_unknown")).thenReturn("1");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "named_graph_score_weight")).thenReturn("0.8");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "publisher_score_weight")).thenReturn("0.2");
        Mockito.when(properties.getProperty(
                ConflictResolutionConfig.GROUP_PREFIX + "max_date_difference")).thenReturn("316.22400");

        ConflictResolutionConfig.load(properties);
    }
}
