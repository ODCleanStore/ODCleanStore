package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.DistanceMeasure;
import cz.cuni.mff.odcleanstore.conflictresolution._AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution._NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.crold.aggregation.AggregationMethodBase;
import cz.cuni.mff.odcleanstore.crold.aggregation.DistanceMetricImpl;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

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
                _AggregationSpec aggregationSpec,
                UniqueURIGenerator uriGenerator,
                DistanceMeasure distanceMetric,
                ConflictResolutionConfig globalConfig) {
            super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
        }

        @Override
        public Collection<ResolvedStatement> aggregate(Collection<Statement> conflictingTriples, _NamedGraphMetadataMap metadata) {
            return Collections.<ResolvedStatement>emptySet();
        }

        @Override
        protected double computeBasicQuality(Statement resultQuad, Collection<String> sourceNamedGraphs,
                _NamedGraphMetadataMap metadata) {
            return globalConfig.getScoreIfUnknown();
        }
    }

    @Test
    public void testSourceNamedGraphsForObject() {
        Collection<Statement> conflictingQuads = new LinkedList<Statement>();
        String subjectURI = CRTestUtils.getUniqueURI();
        String predicateURI = CRTestUtils.getUniqueURI();
        String testedObjectURI = CRTestUtils.getUniqueURI();
        String namedGraphA = CRTestUtils.getUniqueURI();
        String namedGraphB = CRTestUtils.getUniqueURI();

        Statement quadA1 = CRTestUtils.createStatement(subjectURI, predicateURI,
                testedObjectURI, namedGraphA);
        Statement quadA2 = CRTestUtils.createStatement(subjectURI, predicateURI,
                testedObjectURI, namedGraphA);
        Statement quadB = CRTestUtils.createStatement(subjectURI, predicateURI,
                testedObjectURI, namedGraphB);

        conflictingQuads.add(quadA1);
        conflictingQuads.add(CRTestUtils.createStatement(
                subjectURI, predicateURI, CRTestUtils.getUniqueURI()));
        conflictingQuads.add(quadA2);
        conflictingQuads.add(CRTestUtils.createStatement(
                subjectURI, predicateURI, CRTestUtils.getUniqueURI()));
        conflictingQuads.add(quadB);
        conflictingQuads.add(CRTestUtils.createStatement(
                subjectURI, predicateURI, CRTestUtils.getUniqueURI()));

        ConflictResolutionConfig globalConfig = CRTestUtils.createConflictResolutionConfigMock();
        AggregationMethodBase instance = new AggregationMethodBaseImpl(
                new _AggregationSpec(),
                URI_GENERATOR,
                new DistanceMetricImpl(globalConfig),
                globalConfig);
        Collection<String> actualResult = instance.sourceNamedGraphsForObject(
                ValueFactoryImpl.getInstance().createURI(testedObjectURI),
                conflictingQuads);

        String[] expectedResult = new String[] { namedGraphA, namedGraphB };
        Arrays.sort(expectedResult);
        String[] actualResultArray = actualResult.toArray(new String[0]);
        Arrays.sort(actualResultArray);

        Assert.assertArrayEquals(expectedResult, actualResultArray);
    }

}
