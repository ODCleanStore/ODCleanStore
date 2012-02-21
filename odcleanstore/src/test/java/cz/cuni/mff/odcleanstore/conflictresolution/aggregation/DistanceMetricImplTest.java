package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.graph.BlankTripleItem;
import cz.cuni.mff.odcleanstore.graph.LiteralTripleItem;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import org.junit.Test;
import org.junit.Assert;

/**
 * Tests of requirements according to specification.
 * @author Jan Michelfeit
 */
public class DistanceMetricImplTest {
    private static final double DELTA = 0.0;
    
    @Test
    public void testDifferentTypes() {
        TripleItem uriTripleItem = new URITripleItem(TestUtils.getUniqueURI());
        TripleItem literalTripleItem = new LiteralTripleItem("value");
        TripleItem blankTripleItem = new BlankTripleItem(TestUtils.getUniqueURI());
        
        DistanceMetricImpl instance = new DistanceMetricImpl();
        double expectedResult = 1.0;
        
        double result1 = instance.distance(uriTripleItem, literalTripleItem);
        Assert.assertEquals(expectedResult, result1, DELTA);
        
        double result2 = instance.distance(uriTripleItem, blankTripleItem);
        Assert.assertEquals(expectedResult, result2, DELTA);
        
        double result3 = instance.distance(literalTripleItem, blankTripleItem);
        Assert.assertEquals(expectedResult, result3, DELTA);
    }
    
    @Test
    public void testSameValues() {
        DistanceMetricImpl instance = new DistanceMetricImpl();
        double expectedResult = 0.0;
        
        String uri = TestUtils.getUniqueURI();
        TripleItem uriTripleItem1 = new URITripleItem(uri);
        TripleItem uriTripleItem2 = new URITripleItem(uri);
        double uriDistance = instance.distance(uriTripleItem1, uriTripleItem2);
        Assert.assertEquals(expectedResult, uriDistance, DELTA);
        
        String literalValue = "value";
        TripleItem literalTripleItem1 = new LiteralTripleItem(literalValue);
        TripleItem literalTripleItem2 = new LiteralTripleItem(literalValue);
        double literalDistance = instance.distance(literalTripleItem1, literalTripleItem2);
        Assert.assertEquals(expectedResult, literalDistance, DELTA);
    }
    
    @Test
    public void testSymmetry() {
        DistanceMetricImpl instance = new DistanceMetricImpl();
        
        TripleItem uriTripleItem1 = new URITripleItem(TestUtils.getUniqueURI());
        TripleItem uriTripleItem2 = new URITripleItem(TestUtils.getUniqueURI());
        double uriDistance1 = instance.distance(uriTripleItem1, uriTripleItem2);
        double uriDistance2 = instance.distance(uriTripleItem2, uriTripleItem1);
        Assert.assertEquals(uriDistance1, uriDistance2, DELTA);
        
        TripleItem literalTripleItem1 = new LiteralTripleItem("value1");
        TripleItem literalTripleItem2 = new LiteralTripleItem("value1");
        double literalDistance1 =
                instance.distance(literalTripleItem1, literalTripleItem2);
        double literalDistance2 = 
                instance.distance(literalTripleItem2, literalTripleItem1);
        Assert.assertEquals(literalDistance1, literalDistance2, DELTA);
    }
    
    @Test
    public void testDifferentValues() {
        DistanceMetricImpl instance = new DistanceMetricImpl();
        
        TripleItem uriTripleItem1 = new URITripleItem(TestUtils.getUniqueURI());
        TripleItem uriTripleItem2 = new URITripleItem(TestUtils.getUniqueURI());
        double uriDistance = instance.distance(uriTripleItem1, uriTripleItem2);
        Assert.assertTrue(uriDistance > 0);
        Assert.assertTrue(uriDistance <= 1);
        
        TripleItem literalTripleItem1 = new LiteralTripleItem("value1");
        TripleItem literalTripleItem2 = new LiteralTripleItem("value2");
        double literalDistance =
                instance.distance(literalTripleItem1, literalTripleItem2);
        Assert.assertTrue(literalDistance > 0);
        Assert.assertTrue(literalDistance <= 1);
    }
    
    // testNumericScale
    // testNumericValues
}
