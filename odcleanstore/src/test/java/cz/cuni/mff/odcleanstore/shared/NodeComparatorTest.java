package cz.cuni.mff.odcleanstore.shared;

import cz.cuni.mff.odcleanstore.TestUtils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.AnonId;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Jan Michelfeit
 */
public class NodeComparatorTest {
    @Test
    public void testCompareEquals() {
        int expectedResult = 0;

        String uri = TestUtils.getUniqueURI();
        Node uriNode1 = Node.createURI(uri);
        Node uriNode2 = Node.createURI(uri);
        int uriComparison = NodeComparator.compare(uriNode1, uriNode2);
        Assert.assertEquals(expectedResult, uriComparison);

        String literalValue = "value";
        Node literalNode1 = Node.createLiteral(literalValue);
        Node literalNode2 = Node.createLiteral(literalValue);
        int literalComparison = NodeComparator
                .compare(literalNode1, literalNode2);
        Assert.assertEquals(expectedResult, literalComparison);

        AnonId anonId = new AnonId(TestUtils.getUniqueURI());
        Node blankNode1 = Node.createAnon(anonId);
        Node blankNode2 = Node.createAnon(anonId);
        int blankNodeComparison = NodeComparator.compare(blankNode1, blankNode2);
        Assert.assertEquals(expectedResult, blankNodeComparison);
    }

    @Test
    public void testCompareDifferentTypes() {
        Node uriNode = Node.createURI(TestUtils.getUniqueURI());
        Node literalNode = Node.createLiteral("value");
        Node anonNode = Node.createAnon(new AnonId(TestUtils.getUniqueURI()));

        int result1 = NodeComparator.compare(uriNode, literalNode);
        Assert.assertTrue(result1 != 0);

        int result2 = NodeComparator.compare(uriNode, anonNode);
        Assert.assertTrue(result2 != 0);

        int result3 = NodeComparator.compare(literalNode, anonNode);
        Assert.assertTrue(result3 != 0);
    }

    @Test
    public void testCompareDifferentValues() {
        Node uriNode1 = Node.createURI(TestUtils.getUniqueURI());
        Node uriNode2 = Node.createURI(TestUtils.getUniqueURI());
        int uriResult = NodeComparator.compare(uriNode1, uriNode2);
        Assert.assertTrue(uriResult != 0);

        Node literalNode1 = Node.createLiteral("value1");
        Node literalNode2 = Node.createLiteral("value2");
        int literalResult =
                NodeComparator.compare(literalNode1, literalNode2);
        Assert.assertTrue(literalResult != 0);

        Node blankNode1 = Node.createAnon(new AnonId(TestUtils.getUniqueURI()));
        Node blankNode2 = Node.createAnon(new AnonId(TestUtils.getUniqueURI()));
        int blankNodeResult = NodeComparator.compare(blankNode1, blankNode2);
        Assert.assertTrue(blankNodeResult != 0);
    }
}
