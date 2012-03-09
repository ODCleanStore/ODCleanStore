package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

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

    public class CalculatedValueAggregationImpl extends CalculatedValueAggregation {
        @Override
        public Collection<CRQuad> aggregate(
                Collection<Quad> conflictingQuads,
                NamedGraphMetadataMap metadata,
                EnumAggregationErrorStrategy errorStrategy,
                UniqueURIGenerator uriGenerator) {

            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean isAggregable(Node value) {
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

        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl();

        Quad quad = TestUtils.createQuad();
        Collection<Quad> conflictingQuads = Collections.singletonList(quad);
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();
        NamedGraphMetadata metadata = new NamedGraphMetadata(quad.getGraphName().getURI());
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

        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl();

        Collection<Quad> conflictingQuads = generateQuadCollection();
        NamedGraphMetadataMap metadataMap = new NamedGraphMetadataMap();

        Quad lowerQuad = TestUtils.createQuad();
        NamedGraphMetadata lowerMetadata =
                new NamedGraphMetadata(lowerQuad.getGraphName().getURI());
        lowerMetadata.setScore(lowerScore);
        lowerMetadata.setPublisherScore(lowerScore);
        metadataMap.addMetadata(lowerMetadata);
        conflictingQuads.add(lowerQuad);

        Quad higherQuad = TestUtils.createQuad();
        NamedGraphMetadata higherMetadata =
                new NamedGraphMetadata(higherQuad.getGraphName().getURI());
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

        // For computed values the result should be the same
        Assert.assertTrue(lowerComputedQuality == higherComputedQuality);
    }

    @Test
    public void testQualityEmptyMetadata() {
        CalculatedValueAggregation instance = new CalculatedValueAggregationImpl();

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
}
