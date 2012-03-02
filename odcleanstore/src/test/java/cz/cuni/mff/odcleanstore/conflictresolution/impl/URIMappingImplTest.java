package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.UnexpectedPredicateException;
import cz.cuni.mff.odcleanstore.graph.Triple;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

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
    private URITripleItem sameAsPredicate;

    {
        sameAsPredicate = new URITripleItem(OWL.sameAs);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestUtils.resetURICounter();
    }

    private String getMappedURI(String uri, URIMapping mapping) {
        String mappedURI = mapping.mapURI(uri);
        return (mappedURI == null) ? uri : mappedURI;
    }

    @Test
    public void testEmptyMapping() {
        URIMappingImpl instance = new URIMappingImpl();
        String expResult = null;
        String result = instance.mapURI(TestUtils.getUniqueURI());
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
                new URITripleItem(uri1),
                sameAsPredicate,
                new URITripleItem(uri2)));
        sameAsLinks.add(new Triple(
                new URITripleItem(uri2),
                sameAsPredicate,
                new URITripleItem(uri3)));

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
                new URITripleItem(uri1),
                sameAsPredicate,
                new URITripleItem(uri2)));
        sameAsLinks.add(new Triple(
                new URITripleItem(uri2),
                sameAsPredicate,
                new URITripleItem(uri3)));
        sameAsLinks.add(new Triple(
                new URITripleItem(uri3),
                sameAsPredicate,
                new URITripleItem(uri1)));

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
                new URITripleItem(uri1),
                sameAsPredicate,
                new URITripleItem(uri2)));
        sameAsLinks.add(new Triple(
                new URITripleItem(uri2),
                sameAsPredicate,
                new URITripleItem(uri3)));

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
