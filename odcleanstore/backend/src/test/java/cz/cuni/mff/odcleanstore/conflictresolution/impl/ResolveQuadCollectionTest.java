package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.data.QuadCollection;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Jan Michelfeit
 */
public class ResolveQuadCollectionTest {

    private static class SingleUriMapping implements URIMapping {
        private Node what;
        private Node mapTo;

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
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestUtils.resetURICounter();
    }

    @Test
    public void testAddQuads() {
        final int quadCount = 3;
        ResolveQuadCollection instance = new ResolveQuadCollection();

        LinkedList<Quad> quadList = new LinkedList<Quad>();
        for (int i = 0; i < quadCount; i++) {
            quadList.add(TestUtils.createQuad());
        }
        QuadCollection quadGraph = new QuadCollection(quadList);
        instance.addQuads(quadGraph);

        Iterator<Collection<Quad>> clusterIterator = instance.listConflictingQuads();
        int clusterCount = 0;
        while (clusterIterator.hasNext()) {
            Collection<Quad> cluster = clusterIterator.next();
            Assert.assertTrue(cluster.size() == 1);
            Quad containedQuad = cluster.iterator().next();
            Assert.assertTrue(TestUtils.inCollection(containedQuad, quadList));
            clusterCount++;
        }
        Assert.assertTrue(clusterCount == quadCount);
    }

    @Test
    public void testApplyMapping() {
        final String subjectURI = TestUtils.getUniqueURI();
        final String predicateURI = TestUtils.getUniqueURI();
        final String objectURI = TestUtils.getUniqueURI();
        final String namedGraph = TestUtils.getUniqueURI();
        final String mappedSubjectURI = TestUtils.getUniqueURI();

        LinkedList<Quad> quadList = new LinkedList<Quad>();
        quadList.add(TestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURI,
                namedGraph));
        QuadCollection quadGraph = new QuadCollection(quadList);

        ResolveQuadCollection instance = new ResolveQuadCollection();
        instance.addQuads(quadGraph);
        instance.applyMapping(new SingleUriMapping(subjectURI, mappedSubjectURI));

        Iterator<Collection<Quad>> clusterIterator = instance.listConflictingQuads();
        Assert.assertTrue(clusterIterator.hasNext());
        Collection<Quad> cluster = clusterIterator.next();
        Assert.assertTrue(cluster.size() == 1);
        Quad containedQuad = cluster.iterator().next();
        Quad expectedQuad = TestUtils.createQuad(
                mappedSubjectURI,
                predicateURI,
                objectURI,
                namedGraph);
        Assert.assertTrue(TestUtils.quadsEquals(containedQuad, expectedQuad));

        Assert.assertFalse(clusterIterator.hasNext());
    }

    @Test
    public void testConflictingQuads() {
        final String subjectURI = TestUtils.getUniqueURI();
        final String predicateURI = TestUtils.getUniqueURI();
        final String objectURI1 = TestUtils.getUniqueURI();
        final String objectURI2 = TestUtils.getUniqueURI();
        final String mappedSubjectURI = TestUtils.getUniqueURI();
        Quad conflictingQuad1 = TestUtils.createQuad(
                subjectURI,
                predicateURI,
                objectURI1);
        Quad mappedConflictingQuad1 = TestUtils.createQuad(
                mappedSubjectURI,
                predicateURI,
                objectURI1);
        Quad conflictingQuad2 = TestUtils.createQuad(
                mappedSubjectURI,
                predicateURI,
                objectURI2);
        Quad otherQuad = TestUtils.createQuad();

        LinkedList<Quad> quadList = new LinkedList<Quad>();
        quadList.add(conflictingQuad1);
        quadList.add(conflictingQuad2);
        quadList.add(otherQuad);
        QuadCollection quadGraph = new QuadCollection(quadList);

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
        TestUtils.inCollection(otherQuad, otherCluster);

        // Test cluster {conflictingQuad2, mappedConflictingQuad1}
        Assert.assertNotNull(conflictingCluster);
        Assert.assertTrue(conflictingCluster.size() == 2);
        TestUtils.inCollection(mappedConflictingQuad1, conflictingCluster);
        TestUtils.inCollection(conflictingQuad2, conflictingCluster);
    }
}
