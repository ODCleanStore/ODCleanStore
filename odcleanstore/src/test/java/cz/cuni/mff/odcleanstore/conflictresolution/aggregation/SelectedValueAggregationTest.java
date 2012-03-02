package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * 
 * @author Jan Michelfeit
 */
public class SelectedValueAggregationTest {
    private static final double EPSILON = 0.0;
    private static final int CONFLICTING_QUAD_COUNT = 3;
    private static final double DEFAULT_SCORE = Math.PI - Math.floor(Math.PI); // 0.14...

    public class SelectedValueAggregationImpl extends SelectedValueAggregation {
        @Override
        public Collection<CRQuad> aggregate(
                Collection<Quad> conflictingQuads,
                NamedGraphMetadataMap metadata,
                AggregationErrorStrategy errorStrategy,
                UniqueURIGenerator uriGenerator) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean isAggregable(TripleItem value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> sourceNamedGraphsForObject(
                TripleItem object, Collection<Quad> conflictingQuads) {
            return super.sourceNamedGraphsForObject(object, conflictingQuads);
        }

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestUtils.resetURICounter();
    }

    private Collection<Quad> generateQuadCollection() {
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        for (int i = 0; i < CONFLICTING_QUAD_COUNT; i++) {
            conflictingQuads.add(TestUtils.createQuad());
        }
        return conflictingQuads;
    }

    @Test
    public void testQualitySingleValue() {
        final double score = DEFAULT_SCORE;

        SelectedValueAggregation instance = new SelectedValueAggregationImpl();

        Quad quad = TestUtils.createQuad();
        Collection<Quad> conflictingQuads = Collections.singletonList(quad);
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();
        NamedGraphMetadata metadata = new NamedGraphMetadata(quad.getNamedGraph());
        metadata.setScore(score);
        metadata.setPublisherScore(score);
        metadataMap.addMetadata(metadata);

        double computedQuality = instance.computeQuality(
                quad,
                conflictingQuads,
                metadataMap);
        Assert.assertEquals(score, computedQuality, EPSILON);
    }

    @Test
    public void testQualityIncreasingScore() {
        // We're relying on the fact that all pairs of URI resources have the
        // same distance
        final double lowerScore = DEFAULT_SCORE / 2;
        final double higherScore = DEFAULT_SCORE;

        SelectedValueAggregation instance = new SelectedValueAggregationImpl();

        Collection<Quad> conflictingQuads = generateQuadCollection();
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();

        Quad lowerQuad = TestUtils.createQuad();
        NamedGraphMetadata lowerMetadata =
                new NamedGraphMetadata(lowerQuad.getNamedGraph());
        lowerMetadata.setScore(lowerScore);
        lowerMetadata.setPublisherScore(lowerScore);
        metadataMap.addMetadata(lowerMetadata);
        conflictingQuads.add(lowerQuad);

        Quad higherQuad = TestUtils.createQuad();
        NamedGraphMetadata higherMetadata =
                new NamedGraphMetadata(higherQuad.getNamedGraph());
        higherMetadata.setScore(higherScore);
        higherMetadata.setPublisherScore(higherScore);
        metadataMap.addMetadata(higherMetadata);
        conflictingQuads.add(higherQuad);

        double lowerComputedQuality = instance.computeQuality(
                lowerQuad,
                conflictingQuads,
                metadataMap);
        double higherComputedQuality = instance.computeQuality(
                higherQuad,
                conflictingQuads,
                metadataMap);
        Assert.assertTrue(lowerComputedQuality < higherComputedQuality);
    }

    @Test
    public void testQualityResourceDifference() {
        String subjectURI = TestUtils.getUniqueURI();
        String predicateURI = TestUtils.getUniqueURI();
        String objectURIDouble = TestUtils.getUniqueURI();
        String objectURISingle = TestUtils.getUniqueURI();

        Collection<Quad> conflictingQuads = generateQuadCollection();
        Quad doubleQuad1 = TestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURIDouble);
        conflictingQuads.add(doubleQuad1);
        Quad doubleQuad2 = TestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURIDouble);
        conflictingQuads.add(doubleQuad2);
        Quad singleQuad = TestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURISingle);
        conflictingQuads.add(singleQuad);

        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();
        SelectedValueAggregation instance = new SelectedValueAggregationImpl();

        double calculatedValueDouble = instance.computeQuality(
                doubleQuad1,
                conflictingQuads,
                metadataMap);
        double calculatedValueSingle = instance.computeQuality(
                singleQuad,
                conflictingQuads,
                metadataMap);
        Assert.assertTrue(calculatedValueSingle < calculatedValueDouble);
    }

    @Test
    public void testQualityEmptyMetadata() {
        SelectedValueAggregation instance = new SelectedValueAggregationImpl();

        Quad quad = TestUtils.createQuad();
        Collection<Quad> conflictingQuads = Collections.singletonList(quad);
        NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();

        double computedQuality = instance.computeQuality(
                quad,
                conflictingQuads,
                metadata);
        Assert.assertEquals(
                AggregationMethodBase.SCORE_IF_UNKNOWN,
                computedQuality,
                EPSILON);
    }

    @Test
    public void testSourceNamedGraphsForObject() {
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        String subjectURI = TestUtils.getUniqueURI();
        String predicateURI = TestUtils.getUniqueURI();
        String testedObjectURI = TestUtils.getUniqueURI();
        String namedGraphA = TestUtils.getUniqueURI();
        String namedGraphB = TestUtils.getUniqueURI();

        Quad quadA1 = TestUtils.createQuad(subjectURI, predicateURI,
                testedObjectURI, namedGraphA);
        Quad quadA2 = TestUtils.createQuad(subjectURI, predicateURI,
                testedObjectURI, namedGraphA);
        Quad quadB = TestUtils.createQuad(subjectURI, predicateURI,
                testedObjectURI, namedGraphB);

        conflictingQuads.add(quadA1);
        conflictingQuads.add(TestUtils.createQuad(
                subjectURI, predicateURI, TestUtils.getUniqueURI()));
        conflictingQuads.add(quadA2);
        conflictingQuads.add(TestUtils.createQuad(
                subjectURI, predicateURI, TestUtils.getUniqueURI()));
        conflictingQuads.add(quadB);
        conflictingQuads.add(TestUtils.createQuad(
                subjectURI, predicateURI, TestUtils.getUniqueURI()));

        SelectedValueAggregation instance = new SelectedValueAggregationImpl();
        Collection<String> actualResult = instance.sourceNamedGraphsForObject(
                new URITripleItem(testedObjectURI),
                conflictingQuads);

        String[] expectedResult = new String[] { namedGraphA, namedGraphB };
        Arrays.sort(expectedResult);
        String[] actualResultArray = actualResult.toArray(new String[0]);
        Arrays.sort(actualResultArray);

        Assert.assertArrayEquals(expectedResult, actualResultArray);
    }
}
