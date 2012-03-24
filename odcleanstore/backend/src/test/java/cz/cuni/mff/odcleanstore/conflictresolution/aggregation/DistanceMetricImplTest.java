package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.TestUtils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests of requirements according to specification.
 * @todo
 * @author Jan Michelfeit
 */
public class DistanceMetricImplTest {
    private static final double DELTA = 0.0;

    @Test
    public void testDifferentTypes() {
        Node uriNode = Node.createURI(TestUtils.getUniqueURI());
        Node literalNode = Node.createLiteral("value");
        Node blankNode = Node.createAnon(new AnonId(TestUtils.getUniqueURI()));

        DistanceMetricImpl instance = new DistanceMetricImpl();
        double expectedResult = 1.0;

        double result1 = instance.distance(uriNode, literalNode);
        Assert.assertEquals(expectedResult, result1, DELTA);

        double result2 = instance.distance(uriNode, blankNode);
        Assert.assertEquals(expectedResult, result2, DELTA);

        double result3 = instance.distance(literalNode, blankNode);
        Assert.assertEquals(expectedResult, result3, DELTA);
    }

    @Test
    public void testSameValues() {
        DistanceMetricImpl instance = new DistanceMetricImpl();
        double expectedResult = 0.0;

        String uri = TestUtils.getUniqueURI();
        Node uriNode1 = Node.createURI(uri);
        Node uriNode2 = Node.createURI(uri);
        double uriDistance = instance.distance(uriNode1, uriNode2);
        Assert.assertEquals(expectedResult, uriDistance, DELTA);

        String literalValue = "value";
        Node literalNode1 = Node.createLiteral(literalValue);
        Node literalNode2 = Node.createLiteral(literalValue);
        double literalDistance = instance.distance(literalNode1, literalNode2);
        Assert.assertEquals(expectedResult, literalDistance, DELTA);
    }

    @Test
    public void testSymmetry() {
        DistanceMetricImpl instance = new DistanceMetricImpl();

        Node uriNode1 = Node.createURI(TestUtils.getUniqueURI());
        Node uriNode2 = Node.createURI(TestUtils.getUniqueURI());
        double uriDistance1 = instance.distance(uriNode1, uriNode2);
        double uriDistance2 = instance.distance(uriNode2, uriNode1);
        Assert.assertEquals(uriDistance1, uriDistance2, DELTA);

        Node literalNode1 = Node.createLiteral("value1");
        Node literalNode2 = Node.createLiteral("value1");
        double literalDistance1 =
                instance.distance(literalNode1, literalNode2);
        double literalDistance2 =
                instance.distance(literalNode2, literalNode1);
        Assert.assertEquals(literalDistance1, literalDistance2, DELTA);
    }

    @Test
    public void testDifferentValues() {
        DistanceMetricImpl instance = new DistanceMetricImpl();

        Node uriNode1 = Node.createURI(TestUtils.getUniqueURI());
        Node uriNode2 = Node.createURI(TestUtils.getUniqueURI());
        double uriDistance = instance.distance(uriNode1, uriNode2);
        Assert.assertTrue(uriDistance > 0);
        Assert.assertTrue(uriDistance <= 1);

        Node literalNode1 = Node.createLiteral("value1");
        Node literalNode2 = Node.createLiteral("value2");
        double literalDistance =
                instance.distance(literalNode1, literalNode2);
        Assert.assertTrue(literalDistance > 0);
        Assert.assertTrue(literalDistance <= 1);
    }

    // testNumericScale
    // testNumericValues
}
