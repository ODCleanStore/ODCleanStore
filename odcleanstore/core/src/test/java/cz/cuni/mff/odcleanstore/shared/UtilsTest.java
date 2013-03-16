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
        Assert.assertTrue(ODCSUtils.isValidIRI("http://example.com"));
        Assert.assertTrue(ODCSUtils.isValidIRI("abc:def"));

        Assert.assertFalse(ODCSUtils.isValidIRI(""));
        Assert.assertFalse(ODCSUtils.isValidIRI("http://exam ple.com"));
        Assert.assertFalse(ODCSUtils.isValidIRI("http://exam`ple.com"));
        Assert.assertFalse(ODCSUtils.isValidIRI("<http://example.com>"));
        Assert.assertFalse(ODCSUtils.isValidIRI(" http://example.com "));
    }

    @Test
    public void testIsPrefixedName() {
        Assert.assertTrue(ODCSUtils.isPrefixedName("abc:def"));
        Assert.assertTrue(ODCSUtils.isPrefixedName("abc:0123def"));
        Assert.assertTrue(ODCSUtils.isPrefixedName("abc:"));
        Assert.assertTrue(ODCSUtils.isPrefixedName(":def"));
        Assert.assertTrue(ODCSUtils.isPrefixedName(":"));

        Assert.assertFalse(ODCSUtils.isPrefixedName(""));
        Assert.assertFalse(ODCSUtils.isPrefixedName("abcdef"));
        Assert.assertFalse(ODCSUtils.isPrefixedName("abc.:def"));
        Assert.assertFalse(ODCSUtils.isPrefixedName("abc:def."));
        Assert.assertFalse(ODCSUtils.isPrefixedName(" abc:def "));
    }

}
