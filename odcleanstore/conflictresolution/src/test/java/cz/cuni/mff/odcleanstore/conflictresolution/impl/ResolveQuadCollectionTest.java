package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;

/**
 *
 * @author Jan Michelfeit
 */
public class ResolveQuadCollectionTest {

    private static class SingleUriMapping implements URIMapping {
        private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
        
        private final URI what;
        private final URI mapTo;

        public SingleUriMapping(String what, String mapTo) {
            this.what = VALUE_FACTORY.createURI(what);
            this.mapTo = VALUE_FACTORY.createURI(mapTo);
        }

        @Override
        public URI mapURI(URI uri) {
            if (uri.equals(what)) {
                return mapTo;
            } else {
                return null;
            }
        }

        @Override
        public String getCanonicalURI(String uri) {
            if (uri.equals(what.stringValue())) {
                return mapTo.stringValue();
            } else {
                return uri;
            }
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        CRTestUtils.resetURICounter();
    }

    @Test
    public void testAddQuads() {
        final int quadCount = 3;
        ResolveQuadCollection instance = new ResolveQuadCollection();

        LinkedList<Statement> quadList = new LinkedList<Statement>();
        for (int i = 0; i < quadCount; i++) {
            quadList.add(CRTestUtils.createStatement());
        }
        Collection<Statement> quadGraph = new ArrayList<Statement>((quadList));
        instance.addQuads(quadGraph);

        Iterator<Collection<Statement>> clusterIterator = instance.listConflictingQuads();
        int clusterCount = 0;
        while (clusterIterator.hasNext()) {
            Collection<Statement> cluster = clusterIterator.next();
            Assert.assertTrue(cluster.size() == 1);
            Statement containedQuad = cluster.iterator().next();
            Assert.assertTrue(CRTestUtils.inCollection(containedQuad, quadList));
            clusterCount++;
        }
        Assert.assertTrue(clusterCount == quadCount);
    }

    @Test
    public void testApplyMapping() {
        final String subjectURI = CRTestUtils.getUniqueURI();
        final String predicateURI = CRTestUtils.getUniqueURI();
        final String objectURI = CRTestUtils.getUniqueURI();
        final String namedGraph = CRTestUtils.getUniqueURI();
        final String mappedSubjectURI = CRTestUtils.getUniqueURI();

        LinkedList<Statement> quadList = new LinkedList<Statement>();
        quadList.add(CRTestUtils.createStatement(
                subjectURI,
                predicateURI,
                objectURI,
                namedGraph));
        Collection<Statement> quadGraph = new ArrayList<Statement>((quadList));

        ResolveQuadCollection instance = new ResolveQuadCollection();
        instance.addQuads(quadGraph);
        instance.applyMapping(new SingleUriMapping(subjectURI, mappedSubjectURI));

        Iterator<Collection<Statement>> clusterIterator = instance.listConflictingQuads();
        Assert.assertTrue(clusterIterator.hasNext());
        Collection<Statement> cluster = clusterIterator.next();
        Assert.assertTrue(cluster.size() == 1);
        Statement containedQuad = cluster.iterator().next();
        Statement expectedQuad = CRTestUtils.createStatement(
                mappedSubjectURI,
                predicateURI,
                objectURI,
                namedGraph);
        Assert.assertTrue(CRTestUtils.statementsEqual(containedQuad, expectedQuad));

        Assert.assertFalse(clusterIterator.hasNext());
    }

    @Test
    public void testConflictingQuads() {
        final String subjectURI = CRTestUtils.getUniqueURI();
        final String predicateURI = CRTestUtils.getUniqueURI();
        final String objectURI1 = CRTestUtils.getUniqueURI();
        final String objectURI2 = CRTestUtils.getUniqueURI();
        final String mappedSubjectURI = CRTestUtils.getUniqueURI();
        Statement conflictingQuad1 = CRTestUtils.createStatement(
                subjectURI,
                predicateURI,
                objectURI1);
        Statement mappedConflictingQuad1 = CRTestUtils.createStatement(
                mappedSubjectURI,
                predicateURI,
                objectURI1);
        Statement conflictingQuad2 = CRTestUtils.createStatement(
                mappedSubjectURI,
                predicateURI,
                objectURI2);
        Statement otherQuad = CRTestUtils.createStatement();

        LinkedList<Statement> quadList = new LinkedList<Statement>();
        quadList.add(conflictingQuad1);
        quadList.add(conflictingQuad2);
        quadList.add(otherQuad);
        Collection<Statement> quadGraph = new ArrayList<Statement>((quadList));

        ResolveQuadCollection instance = new ResolveQuadCollection();
        instance.addQuads(quadGraph);
        instance.applyMapping(new SingleUriMapping(subjectURI, mappedSubjectURI));

        // Now instance should contain two clusters:
        // {conflictingQuad2, mappedConflictingQuad1} and { otherQuad }
        Collection<Statement> conflictingCluster = null;
        Collection<Statement> otherCluster = null;

        Iterator<Collection<Statement>> clusterIterator = instance.listConflictingQuads();
        while (clusterIterator.hasNext()) {
            Collection<Statement> cluster = clusterIterator.next();
            if (cluster.size() == 1) {
                otherCluster = cluster;
            } else if (cluster.size() == 2) {
                conflictingCluster = cluster;
            } else {
                Assert.fail();
            }
        }

        // Test cluster { otherQuad }
        Assert.assertNotNull(otherCluster);
        Assert.assertTrue(otherCluster.size() == 1);
        CRTestUtils.inCollection(otherQuad, otherCluster);

        // Test cluster {conflictingQuad2, mappedConflictingQuad1}
        Assert.assertNotNull(conflictingCluster);
        Assert.assertTrue(conflictingCluster.size() == 2);
        CRTestUtils.inCollection(mappedConflictingQuad1, conflictingCluster);
        CRTestUtils.inCollection(conflictingQuad2, conflictingCluster);
    }
}
