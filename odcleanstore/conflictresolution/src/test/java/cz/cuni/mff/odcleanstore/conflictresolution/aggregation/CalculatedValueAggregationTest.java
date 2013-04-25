package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 *
 * @author Jan Michelfeit
 */
public class CalculatedValueAggregationTest {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
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
                _AggregationSpec aggregationSpec,
                UniqueURIGenerator uriGenerator,
                DistanceMeasure distanceMetric,
                ConflictResolutionConfig globalConfig) {
            super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        }

        @Override
        public Collection<ResolvedStatement> aggregate(
                Collection<Statement> conflictingQuads, _NamedGraphMetadataMap metadata) {

            throw new UnsupportedOperationException();
        }
    }

    public static void setUpClass() throws Exception {
        CRTestUtils.resetURICounter();
    }

    private Collection<Statement> generateQuadCollection() {
        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
        for (int i = 0; i < CONFLICTING_QUAD_COUNT; i++) {
            conflictingQuads.add(CRTestUtils.createStatement());
        }
        return conflictingQuads;
    }

    @Test
    public void testQualitySingleValue() {
        final double score = DEFAULT_SCORE;

        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl (
                new _AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Statement quad = CRTestUtils.createStatement();
        Collection<String> sourceNamedGraphs = Collections.singleton(quad.getContext().stringValue());
        _NamedGraphMetadataMap metadataMap = new _NamedGraphMetadataMap();
        _NamedGraphMetadata metadata = new _NamedGraphMetadata(quad.getContext().stringValue());
        metadata.setScore(score);
        metadata.setTotalPublishersScore(score);
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

        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl (
                new _AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Collection<Statement> conflictingQuads = generateQuadCollection();
        _NamedGraphMetadataMap metadataMap = new _NamedGraphMetadataMap();

        Statement lowerQuad = CRTestUtils.createStatement();
        _NamedGraphMetadata lowerMetadata =
                new _NamedGraphMetadata(lowerQuad.getContext().stringValue());
        lowerMetadata.setScore(lowerScore);
        lowerMetadata.setTotalPublishersScore(lowerScore);
        metadataMap.addMetadata(lowerMetadata);
        conflictingQuads.add(lowerQuad);

        Statement higherQuad = CRTestUtils.createStatement();
        _NamedGraphMetadata higherMetadata =
                new _NamedGraphMetadata(higherQuad.getContext().stringValue());
        higherMetadata.setScore(higherScore);
        higherMetadata.setTotalPublishersScore(higherScore);
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
        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl (
                new _AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Statement quad = CRTestUtils.createStatement();
        Collection<String> sourceNamedGraphs = Collections.singleton(quad.getContext().stringValue());
        _NamedGraphMetadataMap metadata = new _NamedGraphMetadataMap();

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
