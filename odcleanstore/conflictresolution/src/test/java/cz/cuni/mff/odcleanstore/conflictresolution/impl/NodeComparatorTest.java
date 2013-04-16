package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;

/**
 *
 * @author Jan Michelfeit
 */
public class NodeComparatorTest {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
    
    @Test
    public void testCompareEquals() {
        int expectedResult = 0;

        String uri = CRTestUtils.getUniqueURI();
        URI uriNode1 = VALUE_FACTORY.createURI(uri);
        URI uriNode2 = VALUE_FACTORY.createURI(uri);
        int uriComparison = ValueComparator.getInstance().compare(uriNode1, uriNode2);
        Assert.assertEquals(expectedResult, uriComparison);

        String literalValue = "value";
        Literal literalNode1 = VALUE_FACTORY.createLiteral(literalValue);
        Literal literalNode2 = VALUE_FACTORY.createLiteral(literalValue);
        int literalComparison = ValueComparator.getInstance()
                .compare(literalNode1, literalNode2);
        Assert.assertEquals(expectedResult, literalComparison);

        String anonId = CRTestUtils.getUniqueURI();
        BNode blankNode1 = VALUE_FACTORY.createBNode(anonId);
        BNode blankNode2 = VALUE_FACTORY.createBNode(anonId);
        int blankNodeComparison = ValueComparator.getInstance().compare(blankNode1, blankNode2);
        Assert.assertEquals(expectedResult, blankNodeComparison);
    }

    @Test
    public void testCompareDifferentTypes() {
        Value uriNode = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        Value literalNode = VALUE_FACTORY.createLiteral("value");
        Value anonNode = VALUE_FACTORY.createBNode(CRTestUtils.getUniqueURI());

        int result1 = ValueComparator.getInstance().compare(uriNode, literalNode);
        Assert.assertTrue(result1 != 0);

        int result2 = ValueComparator.getInstance().compare(uriNode, anonNode);
        Assert.assertTrue(result2 != 0);

        int result3 = ValueComparator.getInstance().compare(literalNode, anonNode);
        Assert.assertTrue(result3 != 0);
    }

    @Test
    public void testCompareDifferentValues() {
        Value uriNode1 = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        Value uriNode2 = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        int uriResult = ValueComparator.getInstance().compare(uriNode1, uriNode2);
        Assert.assertTrue(uriResult != 0);

        Value literalNode1 = VALUE_FACTORY.createLiteral("value1");
        Value literalNode2 = VALUE_FACTORY.createLiteral("value2");
        int literalResult =
                ValueComparator.getInstance().compare(literalNode1, literalNode2);
        Assert.assertTrue(literalResult != 0);

        Value blankNode1 = VALUE_FACTORY.createBNode((CRTestUtils.getUniqueURI()));
        Value blankNode2 = VALUE_FACTORY.createBNode((CRTestUtils.getUniqueURI()));
        int blankNodeResult = ValueComparator.getInstance().compare(blankNode1, blankNode2);
        Assert.assertTrue(blankNodeResult != 0);
    }
}
