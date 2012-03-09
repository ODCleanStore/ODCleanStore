package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.UnexpectedPredicateException;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author Jan Michelfeit
 */
public class URIMappingImplTest {
    private static Node sameAsPredicate;

    @BeforeClass
    public static void setUpClass() throws Exception {
        sameAsPredicate = Node.createURI(OWL.sameAs);
        TestUtils.resetURICounter();
    }

    private String getMappedURI(String uri, URIMapping mapping) {
        Node mappedURI = mapping.mapURI(Node.createURI(uri));
        return (mappedURI == null) ? uri : mappedURI.getURI();
    }

    @Test
    public void testEmptyMapping() {
        URIMappingImpl instance = new URIMappingImpl();
        String expResult = null;
        Node result = instance.mapURI(Node.createURI(TestUtils.getUniqueURI()));
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testNonEmptyMapping() throws UnexpectedPredicateException {
        String uri1 = TestUtils.getUniqueURI();
        String uri2 = TestUtils.getUniqueURI();
        String uri3 = TestUtils.getUniqueURI();
        String uri4 = TestUtils.getUniqueURI();

        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(new Triple(
                Node.createURI(uri1),
                sameAsPredicate,
                Node.createURI(uri2)));
        sameAsLinks.add(new Triple(
                Node.createURI(uri2),
                sameAsPredicate,
                Node.createURI(uri3)));

        URIMappingImpl uriMapping = new URIMappingImpl();
        uriMapping.addLinks(sameAsLinks.iterator());

        String mappedURI1 = getMappedURI(uri1, uriMapping);
        String mappedURI2 = getMappedURI(uri2, uriMapping);
        String mappedURI3 = getMappedURI(uri3, uriMapping);
        String mappedURI4 = getMappedURI(uri4, uriMapping);

        Assert.assertEquals(mappedURI1, mappedURI2);
        Assert.assertEquals(mappedURI1, mappedURI3);
        Assert.assertFalse(mappedURI4.equals(mappedURI1));
    }

    @Test
    public void testCycleMapping() throws UnexpectedPredicateException {
        String uri1 = TestUtils.getUniqueURI();
        String uri2 = TestUtils.getUniqueURI();
        String uri3 = TestUtils.getUniqueURI();

        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(new Triple(
                Node.createURI(uri1),
                sameAsPredicate,
                Node.createURI(uri2)));
        sameAsLinks.add(new Triple(
                Node.createURI(uri2),
                sameAsPredicate,
                Node.createURI(uri3)));
        sameAsLinks.add(new Triple(
                Node.createURI(uri3),
                sameAsPredicate,
                Node.createURI(uri1)));

        URIMappingImpl uriMapping = new URIMappingImpl();
        uriMapping.addLinks(sameAsLinks.iterator());

        String mappedURI1 = getMappedURI(uri1, uriMapping);
        String mappedURI2 = getMappedURI(uri2, uriMapping);
        String mappedURI3 = getMappedURI(uri3, uriMapping);

        Assert.assertEquals(mappedURI1, mappedURI2);
        Assert.assertEquals(mappedURI1, mappedURI3);
    }

    @Test
    public void testPreferredURIs() throws UnexpectedPredicateException {
        String uri1 = TestUtils.getUniqueURI();
        String uri2 = TestUtils.getUniqueURI();
        String uri3 = TestUtils.getUniqueURI();

        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(new Triple(
                Node.createURI(uri1),
                sameAsPredicate,
                Node.createURI(uri2)));
        sameAsLinks.add(new Triple(
                Node.createURI(uri2),
                sameAsPredicate,
                Node.createURI(uri3)));

        String mappedURI1;

        URIMappingImpl mappingPreferring1 = new URIMappingImpl(Collections.singleton(uri1));
        mappingPreferring1.addLinks(sameAsLinks.iterator());
        mappedURI1 = getMappedURI(uri1, mappingPreferring1);
        Assert.assertEquals(uri1, mappedURI1);

        URIMappingImpl mappingPreferring2 = new URIMappingImpl(Collections.singleton(uri2));
        mappingPreferring2.addLinks(sameAsLinks.iterator());
        mappedURI1 = getMappedURI(uri1, mappingPreferring2);
        Assert.assertEquals(uri2, mappedURI1);

        URIMappingImpl mappingPreferring3 = new URIMappingImpl(Collections.singleton(uri3));
        mappingPreferring3.addLinks(sameAsLinks.iterator());
        mappedURI1 = getMappedURI(uri1, mappingPreferring3);
        Assert.assertEquals(uri3, mappedURI1);
    }
}
