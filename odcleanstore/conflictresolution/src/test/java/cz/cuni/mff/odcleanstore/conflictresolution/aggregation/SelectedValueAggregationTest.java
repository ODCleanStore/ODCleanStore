package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution._AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution._NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution._NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.crold.aggregation.DistanceMetricImpl;
import cz.cuni.mff.odcleanstore.crold.aggregation.SelectedValueAggregation;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * @TODO is it up to date?
 * @author Jan Michelfeit
 */
public class SelectedValueAggregationTest {
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

    private class SelectedValueAggregationImpl extends SelectedValueAggregation {
        public SelectedValueAggregationImpl(
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

        @Override
        public Collection<String> sourceNamedGraphsForObject(
                Value object, Collection<Statement> conflictingQuads) {
            return super.sourceNamedGraphsForObject(object, conflictingQuads);
        }

    }

    @BeforeClass
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
        SelectedValueAggregation instance =
                new SelectedValueAggregationImpl(
                        new _AggregationSpec(),
                        URI_GENERATOR,
                        new DistanceMetricImpl(globalConfig),
                        globalConfig);

        Statement quad = CRTestUtils.createStatement();
        Collection<Statement> conflictingQuads = Collections.singletonList(quad);
        _NamedGraphMetadataMap metadataMap = new _NamedGraphMetadataMap();
        _NamedGraphMetadata metadata = new _NamedGraphMetadata(quad.getContext().stringValue());
        metadata.setScore(score);
        metadata.setTotalPublishersScore(score);
        metadataMap.addMetadata(metadata);

        double computedQuality = instance.computeQualitySelected(
                quad,
                instance.sourceNamedGraphsForObject(quad.getObject(), conflictingQuads),
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

        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        SelectedValueAggregation instance =
                new SelectedValueAggregationImpl(
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

        double lowerComputedQuality = instance.computeQualitySelected(
                lowerQuad,
                instance.sourceNamedGraphsForObject(lowerQuad.getObject(), conflictingQuads),
                conflictingQuads,
                metadataMap);
        double higherComputedQuality = instance.computeQualitySelected(
                higherQuad,
                instance.sourceNamedGraphsForObject(higherQuad.getObject(), conflictingQuads),
                conflictingQuads,
                metadataMap);
        Assert.assertTrue(lowerComputedQuality < higherComputedQuality);
    }

    @Test
    public void testQualityResourceDifference() {
        String subjectURI = CRTestUtils.getUniqueURI();
        String predicateURI = CRTestUtils.getUniqueURI();
        String objectURIDouble = CRTestUtils.getUniqueURI();
        String objectURISingle = CRTestUtils.getUniqueURI();

        Collection<Statement> conflictingQuads = generateQuadCollection();
        Statement doubleQuad1 = CRTestUtils.createStatement(
                subjectURI,
                predicateURI,
                objectURIDouble);
        conflictingQuads.add(doubleQuad1);
        Statement doubleQuad2 = CRTestUtils.createStatement(
                subjectURI,
                predicateURI,
                objectURIDouble);
        conflictingQuads.add(doubleQuad2);
        Statement singleQuad = CRTestUtils.createStatement(
                subjectURI,
                predicateURI,
                objectURISingle);
        conflictingQuads.add(singleQuad);

        _NamedGraphMetadataMap metadataMap = new _NamedGraphMetadataMap();
        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        _AggregationSpec aggregationSpec = new _AggregationSpec();
        aggregationSpec.setDefaultMultivalue(false);
        SelectedValueAggregation instance =
                new SelectedValueAggregationImpl(
                        aggregationSpec,
                        URI_GENERATOR,
                        new DistanceMetricImpl(globalConfig),
                        globalConfig);

        double calculatedValueDouble = instance.computeQualitySelected(
                doubleQuad1,
                instance.sourceNamedGraphsForObject(doubleQuad1.getObject(), conflictingQuads),
                conflictingQuads,
                metadataMap);
        double calculatedValueSingle = instance.computeQualitySelected(
                singleQuad,
                instance.sourceNamedGraphsForObject(singleQuad.getObject(), conflictingQuads),
                conflictingQuads,
                metadataMap);
        Assert.assertTrue(calculatedValueSingle < calculatedValueDouble);
    }

    @Test
    public void testQualityEmptyMetadata() {
        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        SelectedValueAggregation instance = new SelectedValueAggregationImpl(
                new _AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Statement quad = CRTestUtils.createStatement();
        Collection<Statement> conflictingQuads = Collections.singletonList(quad);
        _NamedGraphMetadataMap metadata = new _NamedGraphMetadataMap();

        double computedQuality = instance.computeQualitySelected(quad,
                instance.sourceNamedGraphsForObject(quad.getObject(), conflictingQuads), conflictingQuads, metadata);
        Assert.assertEquals(globalConfig.getScoreIfUnknown(), computedQuality, EPSILON);
    }

}