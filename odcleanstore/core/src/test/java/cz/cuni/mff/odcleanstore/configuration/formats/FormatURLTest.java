package cz.cuni.mff.odcleanstore.configuration.formats;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatURLTest {
    private static final String GROUP_NAME = "group_name";
    private static final String PARAM_NAME = "param_name";

    private ParameterFormat<URL> formatter;

    @Before
    public void setUp() {
        this.formatter = new FormatURL();
    }

    @Test
    public void testValidURI() throws IllegalParameterFormatException, MalformedURLException {
        assertEquals(new URL("http://java.sun.com/j2se/1.3/"),
                formatter.convertValue(GROUP_NAME, PARAM_NAME, "http://java.sun.com/j2se/1.3/"));
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testURINotURL() throws IllegalParameterFormatException {
        formatter.convertValue(GROUP_NAME, PARAM_NAME, "urn:issn:1535-3613");
    }
}
