package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

/**
 * Tests of requirements according to specification.
 * @todo
 * @author Jan Michelfeit
 */
public class DistanceMetricImplTest {
    private static final double DELTA = 0.0;
    private static final double MAX_DISTANCE = 1.0;
    private static final double MIN_DISTANCE = 0.0;
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    @Test
    public void testDifferentTypes() {
        Value uriNode = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        Value literalNode = VALUE_FACTORY.createLiteral("value");
        Value blankNode = VALUE_FACTORY.createBNode(CRTestUtils.getUniqueURI());

        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());
        double expectedResult = MAX_DISTANCE;

        double result1 = instance.distance(uriNode, literalNode);
        Assert.assertEquals(expectedResult, result1, DELTA);

        double result2 = instance.distance(uriNode, blankNode);
        Assert.assertEquals(expectedResult, result2, DELTA);

        double result3 = instance.distance(literalNode, blankNode);
        Assert.assertEquals(expectedResult, result3, DELTA);
    }

    @Test
    public void testSameValues() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());
        double expectedResult = MIN_DISTANCE;

        String uri = CRTestUtils.getUniqueURI();
        Value uriNode1 = VALUE_FACTORY.createURI(uri);
        Value uriNode2 = VALUE_FACTORY.createURI(uri);
        double uriDistance = instance.distance(uriNode1, uriNode2);
        Assert.assertEquals(expectedResult, uriDistance, DELTA);

        String literalValue = "value";
        Value literalNode1 = VALUE_FACTORY.createLiteral(literalValue);
        Value literalNode2 = VALUE_FACTORY.createLiteral(literalValue);
        double literalDistance = instance.distance(literalNode1, literalNode2);
        Assert.assertEquals(expectedResult, literalDistance, DELTA);
    }

    @Test
    public void testSymmetry() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());

        Value uriNode1 = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        Value uriNode2 = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        double uriDistance1 = instance.distance(uriNode1, uriNode2);
        double uriDistance2 = instance.distance(uriNode2, uriNode1);
        Assert.assertEquals(uriDistance1, uriDistance2, DELTA);

        Value literalNode1 = VALUE_FACTORY.createLiteral("value1");
        Value literalNode2 = VALUE_FACTORY.createLiteral("value1");
        double literalDistance1 =
                instance.distance(literalNode1, literalNode2);
        double literalDistance2 =
                instance.distance(literalNode2, literalNode1);
        Assert.assertEquals(literalDistance1, literalDistance2, DELTA);
    }

    @Test
    public void testDifferentValues() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());

        Value uriNode1 = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        Value uriNode2 = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURI());
        double uriDistance = instance.distance(uriNode1, uriNode2);
        Assert.assertTrue(uriDistance > MIN_DISTANCE);
        Assert.assertTrue(uriDistance <= MAX_DISTANCE);

        Value literalNode1 = VALUE_FACTORY.createLiteral("value1");
        Value literalNode2 = VALUE_FACTORY.createLiteral("value2");
        double literalDistance =
                instance.distance(literalNode1, literalNode2);
        Assert.assertTrue(literalDistance > MIN_DISTANCE);
        Assert.assertTrue(literalDistance <= MAX_DISTANCE);
    }

    @Test
    public void testDateDistance() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());

        Value node1 = VALUE_FACTORY.createLiteral("2002-10-10", VALUE_FACTORY.createURI(XMLSchema.dateType));
        Value node2 = VALUE_FACTORY.createLiteral("2002-10-10", VALUE_FACTORY.createURI(XMLSchema.dateType));
        double distance = instance.distance(node1, node2);
        Assert.assertEquals(MIN_DISTANCE, distance, DELTA);

        Value node3 = VALUE_FACTORY.createLiteral("2000-10-10", VALUE_FACTORY.createURI(XMLSchema.dateType));
        distance = instance.distance(node1, node3);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);

        Value node4 = VALUE_FACTORY.createLiteral("2002-10-10T00:00:00+00:00", VALUE_FACTORY.createURI(XMLSchema.dateTimeType));
        distance = instance.distance(node1, node4);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance < MAX_DISTANCE); // intentionally <
    }

    @Test
    public void testDateTimeDistance() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());

        Value node1 = VALUE_FACTORY.createLiteral("2002-10-10T17:10:00+00:00", VALUE_FACTORY.createURI(XMLSchema.dateTimeType));
        Value node2 = VALUE_FACTORY.createLiteral("2002-10-10T17:10:00Z", VALUE_FACTORY.createURI(XMLSchema.dateTimeType));
        double distance = instance.distance(node1, node2);
        Assert.assertEquals(MIN_DISTANCE, distance, DELTA);

        Value node3 = VALUE_FACTORY.createLiteral("2002-10-10T17:10:01+00:00", VALUE_FACTORY.createURI(XMLSchema.dateTimeType));
        distance = instance.distance(node1, node3);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);

        Value node4 = VALUE_FACTORY.createLiteral("0002-10-10T17:10:00+00:00", VALUE_FACTORY.createURI(XMLSchema.dateTimeType));
        distance = instance.distance(node1, node4);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);
    }

    @Test
    public void testTimeDistance() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());

        Value node1 = VALUE_FACTORY.createLiteral("17:10:00+00:00", VALUE_FACTORY.createURI(XMLSchema.timeType));
        Value node2 = VALUE_FACTORY.createLiteral("17:10:00Z", VALUE_FACTORY.createURI(XMLSchema.timeType));
        double distance = instance.distance(node1, node2);
        Assert.assertEquals(MIN_DISTANCE, distance, DELTA);

        Value node3 = VALUE_FACTORY.createLiteral("10:10:00+00:00", VALUE_FACTORY.createURI(XMLSchema.timeType));
        distance = instance.distance(node1, node3);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);
    }

    // testNumericScale
    // testNumericValues
}
