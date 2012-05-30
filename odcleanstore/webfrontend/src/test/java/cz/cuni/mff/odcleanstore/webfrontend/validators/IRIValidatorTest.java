package cz.cuni.mff.odcleanstore.webfrontend.validators;

import static org.junit.Assert.*;
import org.junit.Test;

public class IRIValidatorTest {

	@Test
	public void testValidIRIRecognition() 
	{
		assertTrue(IRIValidator.isValidIRI("http://example.com"));
        assertTrue(IRIValidator.isValidIRI("abc:def"));

        assertFalse(IRIValidator.isValidIRI(""));
        assertFalse(IRIValidator.isValidIRI("http://exam ple.com"));
        assertFalse(IRIValidator.isValidIRI("http://exam`ple.com"));
        assertFalse(IRIValidator.isValidIRI("<http://example.com>"));
        assertFalse(IRIValidator.isValidIRI(" http://example.com "));
	}

}
