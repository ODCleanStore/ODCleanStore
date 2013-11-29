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
public class FormatDoubleTest {
    private static final String PARAM_NAME = "param_name";

    private ParameterFormat<Double> formatter;

    @Before
    public void setUp() {
        this.formatter = new FormatDouble();
    }

    @Test
    public void testDoubleToDouble() throws IllegalParameterFormatException {
        assertEquals(new Double(3.14159265), formatter.convertValue(PARAM_NAME, "3.14159265"));
    }

    @Test
    public void testIntToDouble() throws IllegalParameterFormatException {
        assertEquals(new Double(3), formatter.convertValue( PARAM_NAME, "3"));
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testTwoDecimalPointsError() throws IllegalParameterFormatException {
        formatter.convertValue(PARAM_NAME, "3.14.159265");
    }

    @Test(expected = IllegalParameterFormatException.class)
    public void testMisstypedNumberError() throws IllegalParameterFormatException {
        formatter.convertValue(PARAM_NAME, "31415GF");
    }
}
