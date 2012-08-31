package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author Jan Michelfeit
 */
public class CalculatedValueAggregationTest {
    private static final double EPSILON = 0.0;
    private static final int CONFLICTING_QUAD_COUNT = 3;
    private static final double DEFAULT_SCORE = Math.PI - Math.floor(Math.PI); // 0.14...
    private static final String URI_PREFIX  = "http://example.com/test/";

    private static final UniqueURIGenerator URI_GENERATOR = new UniqueURIGenerator() {
        private int lastNamedGraphId = 0;
        @Override
        public String nextURI() {
            ++lastNamedGraphId;
            return URI_PREFIX + lastNamedGraphId;
        }
    };

    public class CalculatedValueAggregationImpl extends CalculatedValueAggregation {
        public CalculatedValueAggregationImpl(
                AggregationSpec aggregationSpec,
                UniqueURIGenerator uriGenerator,
                DistanceMetric distanceMetric,
                ConflictResolutionConfig globalConfig) {
            super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        }

        @Override
        public Collection<CRQuad> aggregate(
                Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {

            throw new UnsupportedOperationException();
        }
    }

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

        ConflictResolutionConfig globalConfig = TestUtils.createConflictResolutionConfigMock();
        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl (
                new AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Quad quad = TestUtils.createQuad();
        Collection<String> sourceNamedGraphs = Collections.singleton(quad.getGraphName().getURI());
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();
        NamedGraphMetadata metadata = new NamedGraphMetadata(quad.getGraphName().getURI());
        metadata.setScore(score);
        metadata.setPublisherScores(Collections.singletonList(score));
        metadataMap.addMetadata(metadata);

        double computedQuality = instance.computeQuality(
                quad,
                sourceNamedGraphs,
                Collections.<String>emptySet(),
                Collections.singleton(quad),
                metadataMap);
        Assert.assertEquals(score, computedQuality, EPSILON);
    }

    @Test
    public void testQualityIncreasingScore() {
        // We're relying on the fact that all pairs of URI resources have the
        // same distance
        final double lowerScore = DEFAULT_SCORE / 2;
        final double higherScore = DEFAULT_SCORE;

        ConflictResolutionConfig globalConfig = TestUtils.createConflictResolutionConfigMock();
        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl (
                new AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Collection<Quad> conflictingQuads = generateQuadCollection();
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();

        Quad lowerQuad = TestUtils.createQuad();
        NamedGraphMetadata lowerMetadata =
                new NamedGraphMetadata(lowerQuad.getGraphName().getURI());
        lowerMetadata.setScore(lowerScore);
        lowerMetadata.setPublisherScores(Collections.singletonList(lowerScore));
        metadataMap.addMetadata(lowerMetadata);
        conflictingQuads.add(lowerQuad);

        Quad higherQuad = TestUtils.createQuad();
        NamedGraphMetadata higherMetadata =
                new NamedGraphMetadata(higherQuad.getGraphName().getURI());
        higherMetadata.setScore(higherScore);
        higherMetadata.setPublisherScores(Collections.singletonList(higherScore));
        metadataMap.addMetadata(higherMetadata);
        conflictingQuads.add(higherQuad);
        Collection<String> sourceNamedGraphs = instance.allSourceNamedGraphs(conflictingQuads);

        double lowerComputedQuality = instance.computeQuality(
                lowerQuad,
                sourceNamedGraphs,
                Collections.<String>emptySet(),
                Collections.singleton(lowerQuad),
                metadataMap);
        double higherComputedQuality = instance.computeQuality(
                higherQuad,
                sourceNamedGraphs,
                Collections.<String>emptySet(),
                Collections.singleton(higherQuad),
                metadataMap);

        // For computed values the result should be the same
        Assert.assertTrue(lowerComputedQuality == higherComputedQuality);
    }

    @Test
    public void testQualityEmptyMetadata() {
        ConflictResolutionConfig globalConfig = TestUtils.createConflictResolutionConfigMock();
        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl (
                new AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Quad quad = TestUtils.createQuad();
        Collection<String> sourceNamedGraphs = Collections.singleton(quad.getGraphName().getURI());
        NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();

        double computedQuality = instance.computeQuality(
                quad,
                sourceNamedGraphs,
                Collections.<String>emptySet(),
                Collections.singleton(quad),
                metadata);
        Assert.assertEquals(
                globalConfig.getScoreIfUnknown(),
                computedQuality,
                EPSILON);
    }
}
