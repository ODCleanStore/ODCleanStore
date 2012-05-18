package cz.cuni.mff.odcleanstore.shared;

import org.junit.Assert;
import org.junit.Test;

/**
*
* @author Jan Michelfeit
*/
public class UtilsTest {
    @Test
    public void testIsValidIRI() {
        Assert.assertTrue(Utils.isValidIRI("http://example.com"));
        Assert.assertTrue(Utils.isValidIRI("abc:def"));

        Assert.assertFalse(Utils.isValidIRI(""));
        Assert.assertFalse(Utils.isValidIRI("http://exam ple.com"));
        Assert.assertFalse(Utils.isValidIRI("http://exam`ple.com"));
        Assert.assertFalse(Utils.isValidIRI("<http://example.com>"));
        Assert.assertFalse(Utils.isValidIRI(" http://example.com "));
    }

    @Test
    public void testIsPrefixedName() {
        Assert.assertTrue(Utils.isPrefixedName("abc:def"));
        Assert.assertTrue(Utils.isPrefixedName("abc:0123def"));
        Assert.assertTrue(Utils.isPrefixedName("abc:"));
        Assert.assertTrue(Utils.isPrefixedName(":def"));
        Assert.assertTrue(Utils.isPrefixedName(":"));

        Assert.assertFalse(Utils.isPrefixedName(""));
        Assert.assertFalse(Utils.isPrefixedName("abcdef"));
        Assert.assertFalse(Utils.isPrefixedName("abc.:def"));
        Assert.assertFalse(Utils.isPrefixedName("abc:def."));
        Assert.assertFalse(Utils.isPrefixedName(" abc:def "));
    }

}
