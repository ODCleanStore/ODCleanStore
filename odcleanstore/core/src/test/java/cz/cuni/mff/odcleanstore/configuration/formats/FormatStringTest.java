package cz.cuni.mff.odcleanstore.configuration.formats;

import static org.junit.Assert.assertEquals;

import cz.cuni.mff.odcleanstore.configuration.exceptions.IllegalParameterFormatException;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Dušan Rychnovský (dusan.rychnovsky@gmail.com)
 *
 */
public class FormatStringTest {
    private static final String GROUP_NAME = "group_name";
    private static final String PARAM_NAME = "param_name";

    private ParameterFormat<String> formatter;

    @Before
    public void setUp() {
        this.formatter = new FormatString();
    }

    @Test
    public void testStringToString() throws IllegalParameterFormatException {
        assertEquals("pes", formatter.convertValue(GROUP_NAME, PARAM_NAME, "pes"));

        assertEquals("3.14159265", formatter.convertValue(GROUP_NAME, PARAM_NAME, "3.14159265"));
    }
}
