package cz.cuni.mff.odcleanstore.configuration.formats;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatURITest {
    private static final String GROUP_NAME = "group_name";
    private static final String PARAM_NAME = "param_name";

    private ParameterFormat<URI> formatter;

    @Before
    public void setUp() {
        this.formatter = new FormatURI();
    }

    @Test
    public void testValidURI() throws IllegalParameterFormatException, URISyntaxException {
        assertEquals(new URI("mailto:java-net@java.sun.com"),
                formatter.convertValue(GROUP_NAME, PARAM_NAME, "mailto:java-net@java.sun.com"));

        assertEquals(new URI("http://java.sun.com/j2se/1.3/"),
                formatter.convertValue(GROUP_NAME, PARAM_NAME, "http://java.sun.com/j2se/1.3/"));

        assertEquals(new URI("urn:issn:1535-3613"),
                formatter.convertValue(GROUP_NAME, PARAM_NAME, "urn:issn:1535-3613"));
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testMalformedURI() throws IllegalParameterFormatException, URISyntaxException {
        formatter.convertValue(GROUP_NAME, PARAM_NAME, "./resource.txt#frag01#frag02");
    }
}
