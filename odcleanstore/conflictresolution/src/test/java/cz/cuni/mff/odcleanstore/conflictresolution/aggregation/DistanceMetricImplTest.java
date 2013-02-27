package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.AnonId;

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

    @Test
    public void testDifferentTypes() {
        Node uriNode = Node.createURI(CRTestUtils.getUniqueURI());
        Node literalNode = Node.createLiteral("value");
        Node blankNode = Node.createAnon(new AnonId(CRTestUtils.getUniqueURI()));

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
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());

        Node uriNode1 = Node.createURI(CRTestUtils.getUniqueURI());
        Node uriNode2 = Node.createURI(CRTestUtils.getUniqueURI());
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
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());

        Node uriNode1 = Node.createURI(CRTestUtils.getUniqueURI());
        Node uriNode2 = Node.createURI(CRTestUtils.getUniqueURI());
        double uriDistance = instance.distance(uriNode1, uriNode2);
        Assert.assertTrue(uriDistance > MIN_DISTANCE);
        Assert.assertTrue(uriDistance <= MAX_DISTANCE);

        Node literalNode1 = Node.createLiteral("value1");
        Node literalNode2 = Node.createLiteral("value2");
        double literalDistance =
                instance.distance(literalNode1, literalNode2);
        Assert.assertTrue(literalDistance > MIN_DISTANCE);
        Assert.assertTrue(literalDistance <= MAX_DISTANCE);
    }

    @Test
    public void testDateDistance() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());
        RDFDatatype dateDatatype = TypeMapper.getInstance().getSafeTypeByName(XMLSchema.dateType);
        RDFDatatype dateTimeDatatype = TypeMapper.getInstance().getSafeTypeByName(XMLSchema.dateTimeType);

        Node node1 = Node.createLiteral(LiteralLabelFactory.create("2002-10-10", null, dateDatatype));
        Node node2 = Node.createLiteral(LiteralLabelFactory.create("2002-10-10", null, dateDatatype));
        double distance = instance.distance(node1, node2);
        Assert.assertEquals(MIN_DISTANCE, distance, DELTA);

        Node node3 = Node.createLiteral(LiteralLabelFactory.create("2000-10-10", null, dateDatatype));
        distance = instance.distance(node1, node3);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);

        Node node4 = Node.createLiteral(
                LiteralLabelFactory.create("2002-10-10T00:00:00+00:00", null, dateTimeDatatype));
        distance = instance.distance(node1, node4);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance < MAX_DISTANCE); // intentionally <
    }

    @Test
    public void testDateTimeDistance() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());
        RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(XMLSchema.dateTimeType);

        Node node1 = Node.createLiteral(LiteralLabelFactory.create("2002-10-10T17:10:00+00:00", null, datatype));
        Node node2 = Node.createLiteral(LiteralLabelFactory.create("2002-10-10T17:10:00Z", null, datatype));
        double distance = instance.distance(node1, node2);
        Assert.assertEquals(MIN_DISTANCE, distance, DELTA);

        Node node3 = Node.createLiteral(LiteralLabelFactory.create("2002-10-10T17:10:01+00:00", null, datatype));
        distance = instance.distance(node1, node3);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);

        Node node4 = Node.createLiteral(LiteralLabelFactory.create("0002-10-10T17:10:00+00:00", null, datatype));
        distance = instance.distance(node1, node4);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);
    }

    @Test
    public void testTimeDistance() {
        DistanceMetricImpl instance = new DistanceMetricImpl(CRTestUtils.createConflictResolutionConfigMock());
        RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(XMLSchema.timeType);

        Node node1 = Node.createLiteral(LiteralLabelFactory.create("17:10:00+00:00", null, datatype));
        Node node2 = Node.createLiteral(LiteralLabelFactory.create("17:10:00Z", null, datatype));
        double distance = instance.distance(node1, node2);
        Assert.assertEquals(MIN_DISTANCE, distance, DELTA);

        Node node3 = Node.createLiteral(LiteralLabelFactory.create("10:10:00+00:00", null, datatype));
        distance = instance.distance(node1, node3);
        Assert.assertTrue(distance > MIN_DISTANCE);
        Assert.assertTrue(distance <= MAX_DISTANCE);
    }

    // testNumericScale
    // testNumericValues
}
