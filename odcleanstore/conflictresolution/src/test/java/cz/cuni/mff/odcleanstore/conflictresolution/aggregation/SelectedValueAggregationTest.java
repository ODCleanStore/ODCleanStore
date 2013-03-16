package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import de.fuberlin.wiwiss.ng4j.Quad;

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

        @Override
        public Collection<String> sourceNamedGraphsForObject(
                Node object, Collection<Quad> conflictingQuads) {
            return super.sourceNamedGraphsForObject(object, conflictingQuads);
        }

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        CRTestUtils.resetURICounter();
    }

    private Collection<Quad> generateQuadCollection() {
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        for (int i = 0; i < CONFLICTING_QUAD_COUNT; i++) {
            conflictingQuads.add(CRTestUtils.createQuad());
        }
        return conflictingQuads;
    }

    @Test
    public void testQualitySingleValue() {
        final double score = DEFAULT_SCORE;

        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        SelectedValueAggregation instance =
                new SelectedValueAggregationImpl(
                        new AggregationSpec(),
                        URI_GENERATOR,
                        new DistanceMetricImpl(globalConfig),
                        globalConfig);

        Quad quad = CRTestUtils.createQuad();
        Collection<Quad> conflictingQuads = Collections.singletonList(quad);
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();
        NamedGraphMetadata metadata = new NamedGraphMetadata(quad.getGraphName().getURI());
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
                        new AggregationSpec(),
                        URI_GENERATOR,
                        new DistanceMetricImpl(globalConfig),
                        globalConfig);

        Collection<Quad> conflictingQuads = generateQuadCollection();
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();

        Quad lowerQuad = CRTestUtils.createQuad();
        NamedGraphMetadata lowerMetadata =
                new NamedGraphMetadata(lowerQuad.getGraphName().getURI());
        lowerMetadata.setScore(lowerScore);
        lowerMetadata.setTotalPublishersScore(lowerScore);
        metadataMap.addMetadata(lowerMetadata);
        conflictingQuads.add(lowerQuad);

        Quad higherQuad = CRTestUtils.createQuad();
        NamedGraphMetadata higherMetadata =
                new NamedGraphMetadata(higherQuad.getGraphName().getURI());
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

        Collection<Quad> conflictingQuads = generateQuadCollection();
        Quad doubleQuad1 = CRTestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURIDouble);
        conflictingQuads.add(doubleQuad1);
        Quad doubleQuad2 = CRTestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURIDouble);
        conflictingQuads.add(doubleQuad2);
        Quad singleQuad = CRTestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURISingle);
        conflictingQuads.add(singleQuad);

        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();
        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        AggregationSpec aggregationSpec = new AggregationSpec();
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
                new AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);

        Quad quad = CRTestUtils.createQuad();
        Collection<Quad> conflictingQuads = Collections.singletonList(quad);
        NamedGraphMetadataMap metadata = new NamedGraphMetadataMap();

        double computedQuality = instance.computeQualitySelected(quad,
                instance.sourceNamedGraphsForObject(quad.getObject(), conflictingQuads), conflictingQuads, metadata);
        Assert.assertEquals(globalConfig.getScoreIfUnknown(), computedQuality, EPSILON);
    }

}