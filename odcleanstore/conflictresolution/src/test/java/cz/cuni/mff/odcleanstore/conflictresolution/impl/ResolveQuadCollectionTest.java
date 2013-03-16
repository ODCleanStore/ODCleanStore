package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 *
 * @author Jan Michelfeit
 */
public class ResolveQuadCollectionTest {

    private static class SingleUriMapping implements URIMapping {
        private final Node what;
        private final Node mapTo;

        public SingleUriMapping(String what, String mapTo) {
            this.what = Node.createURI(what);
            this.mapTo = Node.createURI(mapTo);
        }

        @Override
        public Node mapURI(Node uri) {
            if (uri.equals(what)) {
                return mapTo;
            } else {
                return null;
            }
        }

        @Override
        public String getCanonicalURI(String uri) {
            if (uri.equals(what.getURI())) {
                return mapTo.getURI();
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

        LinkedList<Quad> quadList = new LinkedList<Quad>();
        for (int i = 0; i < quadCount; i++) {
            quadList.add(CRTestUtils.createQuad());
        }
        Collection<Quad> quadGraph = new ArrayList<Quad>((quadList));
        instance.addQuads(quadGraph);

        Iterator<Collection<Quad>> clusterIterator = instance.listConflictingQuads();
        int clusterCount = 0;
        while (clusterIterator.hasNext()) {
            Collection<Quad> cluster = clusterIterator.next();
            Assert.assertTrue(cluster.size() == 1);
            Quad containedQuad = cluster.iterator().next();
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

        LinkedList<Quad> quadList = new LinkedList<Quad>();
        quadList.add(CRTestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURI,
                namedGraph));
        Collection<Quad> quadGraph = new ArrayList<Quad>((quadList));

        ResolveQuadCollection instance = new ResolveQuadCollection();
        instance.addQuads(quadGraph);
        instance.applyMapping(new SingleUriMapping(subjectURI, mappedSubjectURI));

        Iterator<Collection<Quad>> clusterIterator = instance.listConflictingQuads();
        Assert.assertTrue(clusterIterator.hasNext());
        Collection<Quad> cluster = clusterIterator.next();
        Assert.assertTrue(cluster.size() == 1);
        Quad containedQuad = cluster.iterator().next();
        Quad expectedQuad = CRTestUtils.createQuad(
                mappedSubjectURI,
                predicateURI,
                objectURI,
                namedGraph);
        Assert.assertTrue(CRTestUtils.quadsEquals(containedQuad, expectedQuad));

        Assert.assertFalse(clusterIterator.hasNext());
    }

    @Test
    public void testConflictingQuads() {
        final String subjectURI = CRTestUtils.getUniqueURI();
        final String predicateURI = CRTestUtils.getUniqueURI();
        final String objectURI1 = CRTestUtils.getUniqueURI();
        final String objectURI2 = CRTestUtils.getUniqueURI();
        final String mappedSubjectURI = CRTestUtils.getUniqueURI();
        Quad conflictingQuad1 = CRTestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURI1);
        Quad mappedConflictingQuad1 = CRTestUtils.createQuad(
                mappedSubjectURI,
                predicateURI,
                objectURI1);
        Quad conflictingQuad2 = CRTestUtils.createQuad(
                mappedSubjectURI,
                predicateURI,
                objectURI2);
        Quad otherQuad = CRTestUtils.createQuad();

        LinkedList<Quad> quadList = new LinkedList<Quad>();
        quadList.add(conflictingQuad1);
        quadList.add(conflictingQuad2);
        quadList.add(otherQuad);
        Collection<Quad> quadGraph = new ArrayList<Quad>((quadList));

        ResolveQuadCollection instance = new ResolveQuadCollection();
        instance.addQuads(quadGraph);
        instance.applyMapping(new SingleUriMapping(subjectURI, mappedSubjectURI));

        // Now instance should contain two clusters:
        // {conflictingQuad2, mappedConflictingQuad1} and { otherQuad }
        Collection<Quad> conflictingCluster = null;
        Collection<Quad> otherCluster = null;

        Iterator<Collection<Quad>> clusterIterator = instance.listConflictingQuads();
        while (clusterIterator.hasNext()) {
            Collection<Quad> cluster = clusterIterator.next();
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
