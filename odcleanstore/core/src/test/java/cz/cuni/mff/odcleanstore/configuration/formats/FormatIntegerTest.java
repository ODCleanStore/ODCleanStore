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
public class FormatIntegerTest {
    private static final String GROUP_NAME = "group_name";
    private static final String PARAM_NAME = "param_name";

    private ParameterFormat<Integer> formatter;

    @Before
    public void setUp() {
        this.formatter = new FormatInteger();
    }

    @Test
    public void testIntegerToInteger() throws IllegalParameterFormatException {
        assertEquals(new Integer(3), formatter.convertValue(GROUP_NAME, PARAM_NAME, "3"));
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testDoubleToInteger() throws IllegalParameterFormatException {
        formatter.convertValue(GROUP_NAME, PARAM_NAME, "3.14159265");
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testMisstypedNumberError() throws IllegalParameterFormatException {
        formatter.convertValue(GROUP_NAME, PARAM_NAME, "31415GF");
    }
}
