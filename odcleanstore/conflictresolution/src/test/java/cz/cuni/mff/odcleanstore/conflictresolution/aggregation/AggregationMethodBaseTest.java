package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import de.fuberlin.wiwiss.ng4j.Quad;

public class AggregationMethodBaseTest {
    private static final String URI_PREFIX = "http://example.com/test/";
    private static final UniqueURIGenerator URI_GENERATOR = new UniqueURIGenerator() {
        private int lastNamedGraphId = 0;
        @Override
        public String nextURI() {
            ++lastNamedGraphId;
            return URI_PREFIX + lastNamedGraphId;
        }
    };

    private static class AggregationMethodBaseImpl extends AggregationMethodBase {
        public AggregationMethodBaseImpl(
                AggregationSpec aggregationSpec,
                UniqueURIGenerator uriGenerator,
                DistanceMetric distanceMetric,
                ConflictResolutionConfig globalConfig) {
            super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        }

        @Override
        public Collection<CRQuad> aggregate(Collection<Quad> conflictingTriples, NamedGraphMetadataMap metadata) {
            return Collections.<CRQuad>emptySet();
        }

        @Override
        protected double computeBasicQuality(Quad resultQuad, Collection<String> sourceNamedGraphs,
                NamedGraphMetadataMap metadata) {
            return globalConfig.getScoreIfUnknown();
        }
    }

    @Test
    public void testSourceNamedGraphsForObject() {
        Collection<Quad> conflictingQuads = new LinkedList<Quad>();
        String subjectURI = CRTestUtils.getUniqueURI();
        String predicateURI = CRTestUtils.getUniqueURI();
        String testedObjectURI = CRTestUtils.getUniqueURI();
        String namedGraphA = CRTestUtils.getUniqueURI();
        String namedGraphB = CRTestUtils.getUniqueURI();

        Quad quadA1 = CRTestUtils.createQuad(subjectURI, predicateURI,
                testedObjectURI, namedGraphA);
        Quad quadA2 = CRTestUtils.createQuad(subjectURI, predicateURI,
                testedObjectURI, namedGraphA);
        Quad quadB = CRTestUtils.createQuad(subjectURI, predicateURI,
                testedObjectURI, namedGraphB);

        conflictingQuads.add(quadA1);
        conflictingQuads.add(CRTestUtils.createQuad(
                subjectURI, predicateURI, CRTestUtils.getUniqueURI()));
        conflictingQuads.add(quadA2);
        conflictingQuads.add(CRTestUtils.createQuad(
                subjectURI, predicateURI, CRTestUtils.getUniqueURI()));
        conflictingQuads.add(quadB);
        conflictingQuads.add(CRTestUtils.createQuad(
                subjectURI, predicateURI, CRTestUtils.getUniqueURI()));

        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        AggregationMethodBase instance = new AggregationMethodBaseImpl(
                new AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);
        Collection<String> actualResult = instance.sourceNamedGraphsForObject(
                Node.createURI(testedObjectURI),
                conflictingQuads);

        String[] expectedResult = new String[] { namedGraphA, namedGraphB };
        Arrays.sort(expectedResult);
        String[] actualResultArray = actualResult.toArray(new String[0]);
        Arrays.sort(actualResultArray);

        Assert.assertArrayEquals(expectedResult, actualResultArray);
    }

}
