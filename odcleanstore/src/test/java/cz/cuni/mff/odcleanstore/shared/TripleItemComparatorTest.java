package cz.cuni.mff.odcleanstore.shared;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.graph.BlankTripleItem;
import cz.cuni.mff.odcleanstore.graph.LiteralTripleItem;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author Jan Michelfeit
 */
public class TripleItemComparatorTest {
    @Test
    public void testCompareEquals() {
        int expectedResult = 0;
        
        String uri = TestUtils.getUniqueURI();
        TripleItem uriTripleItem1 = new URITripleItem(uri);
        TripleItem uriTripleItem2 = new URITripleItem(uri);
        int uriComparison = TripleItemComparator.compare(uriTripleItem1, uriTripleItem2);
        Assert.assertEquals(expectedResult, uriComparison);
        
        String literalValue = "value";
        TripleItem literalTripleItem1 = new LiteralTripleItem(literalValue);
        TripleItem literalTripleItem2 = new LiteralTripleItem(literalValue);
        int literalComparison = TripleItemComparator.compare(literalTripleItem1, literalTripleItem2);
        Assert.assertEquals(expectedResult, literalComparison);
        
        String anonId = TestUtils.getUniqueURI();
        TripleItem blankTripleItem1 = new BlankTripleItem(anonId);
        TripleItem blankTripleItem2 = new BlankTripleItem(anonId);
        int blankNodeComparison = TripleItemComparator.compare(blankTripleItem1, blankTripleItem2);
        Assert.assertEquals(expectedResult, blankNodeComparison);
    }
    
    @Test
    public void testCompareDifferentTypes() {
        TripleItem uriTripleItem = new URITripleItem(TestUtils.getUniqueURI());
        TripleItem literalTripleItem = new LiteralTripleItem("value");
        TripleItem blankTripleItem = new BlankTripleItem(TestUtils.getUniqueURI());
        
        int result1 = TripleItemComparator.compare(uriTripleItem, literalTripleItem);
        Assert.assertTrue(result1 != 0);
        
        int result2 = TripleItemComparator.compare(uriTripleItem, blankTripleItem);
        Assert.assertTrue(result2 != 0);
        
        int result3 = TripleItemComparator.compare(literalTripleItem, blankTripleItem);
        Assert.assertTrue(result3 != 0);
    }
    
    @Test
    public void testCompareDifferentValues() {
        TripleItem uriTripleItem1 = new URITripleItem(TestUtils.getUniqueURI());
        TripleItem uriTripleItem2 = new URITripleItem(TestUtils.getUniqueURI());
        int uriResult = TripleItemComparator.compare(uriTripleItem1, uriTripleItem2);
        Assert.assertTrue(uriResult != 0);
        
        TripleItem literalTripleItem1 = new LiteralTripleItem("value1");
        TripleItem literalTripleItem2 = new LiteralTripleItem("value2");
        int literalResult =
                TripleItemComparator.compare(literalTripleItem1, literalTripleItem2);
        Assert.assertTrue(literalResult != 0);
        
        TripleItem blankTripleItem1 = new BlankTripleItem(TestUtils.getUniqueURI());
        TripleItem blankTripleItem2 = new BlankTripleItem(TestUtils.getUniqueURI());
        int blankNodeResult = TripleItemComparator.compare(blankTripleItem1, blankTripleItem2);
        Assert.assertTrue(blankNodeResult != 0);
    }
}
