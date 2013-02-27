package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 *
 * @author Jan Michelfeit
 */
public class URIMappingImplTest {
    private static Node sameAsPredicate;

    @BeforeClass
    public static void setUpClass() throws Exception {
        sameAsPredicate = Node.createURI(OWL.sameAs);
        CRTestUtils.resetURICounter();
    }

    private String getAndTestMappedURI(String uri, URIMapping mapping) {
        Node mappedURI = mapping.mapURI(Node.createURI(uri));
        String mappedByNode = (mappedURI == null) ? uri : mappedURI.getURI();
        String canonicalURI = mapping.getCanonicalURI(uri);
        Assert.assertEquals(mappedByNode, canonicalURI);
        return canonicalURI;
    }

    @Test
    public void testEmptyMapping() {
        URIMappingImpl instance = new URIMappingImpl();
        String expResult = null;
        Node result = instance.mapURI(Node.createURI(CRTestUtils.getUniqueURI()));
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testNonEmptyMapping1() {
        String uri1 = CRTestUtils.getUniqueURI();
        String uri2 = CRTestUtils.getUniqueURI();
        String uri3 = CRTestUtils.getUniqueURI();
        String uri4 = CRTestUtils.getUniqueURI();

        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(new Triple(Node.createURI(uri1), sameAsPredicate, Node.createURI(uri2)));
        sameAsLinks.add(new Triple(Node.createURI(uri2), sameAsPredicate, Node.createURI(uri3)));

        URIMappingImpl uriMapping = new URIMappingImpl();
        uriMapping.addLinks(sameAsLinks.iterator());

        String mappedURI1 = getAndTestMappedURI(uri1, uriMapping);
        String mappedURI2 = getAndTestMappedURI(uri2, uriMapping);
        String mappedURI3 = getAndTestMappedURI(uri3, uriMapping);
        String mappedURI4 = getAndTestMappedURI(uri4, uriMapping);

        Assert.assertEquals(mappedURI1, mappedURI2);
        Assert.assertEquals(mappedURI1, mappedURI3);
        Assert.assertFalse(mappedURI4.equals(mappedURI1));
    }

    @Test
    public void testNonEmptyMapping2() {
        String rootURI = CRTestUtils.getUniqueURI();
        String uri1 = CRTestUtils.getUniqueURI();
        String uri2 = CRTestUtils.getUniqueURI();
        String uri3 = CRTestUtils.getUniqueURI();

        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(new Triple(Node.createURI(rootURI), sameAsPredicate, Node.createURI(uri1)));
        sameAsLinks.add(new Triple(Node.createURI(rootURI), sameAsPredicate, Node.createURI(uri2)));
        sameAsLinks.add(new Triple(Node.createURI(rootURI), sameAsPredicate, Node.createURI(uri3)));

        URIMappingImpl uriMapping = new URIMappingImpl();
        uriMapping.addLinks(sameAsLinks.iterator());

        String rootMappedURI = getAndTestMappedURI(rootURI, uriMapping);
        String mappedURI1 = getAndTestMappedURI(uri1, uriMapping);
        String mappedURI2 = getAndTestMappedURI(uri2, uriMapping);
        String mappedURI3 = getAndTestMappedURI(uri3, uriMapping);

        Assert.assertEquals(rootMappedURI, mappedURI1);
        Assert.assertEquals(rootMappedURI, mappedURI2);
        Assert.assertEquals(rootMappedURI, mappedURI3);
    }

    @Test
    public void testCycleMapping() {
        String uri1 = CRTestUtils.getUniqueURI();
        String uri2 = CRTestUtils.getUniqueURI();
        String uri3 = CRTestUtils.getUniqueURI();

        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(new Triple(Node.createURI(uri1), sameAsPredicate, Node.createURI(uri2)));
        sameAsLinks.add(new Triple(Node.createURI(uri2), sameAsPredicate, Node.createURI(uri3)));
        sameAsLinks.add(new Triple(Node.createURI(uri3), sameAsPredicate, Node.createURI(uri1)));

        URIMappingImpl uriMapping = new URIMappingImpl();
        uriMapping.addLinks(sameAsLinks.iterator());

        String mappedURI1 = getAndTestMappedURI(uri1, uriMapping);
        String mappedURI2 = getAndTestMappedURI(uri2, uriMapping);
        String mappedURI3 = getAndTestMappedURI(uri3, uriMapping);

        Assert.assertEquals(mappedURI1, mappedURI2);
        Assert.assertEquals(mappedURI1, mappedURI3);
    }

    @Test
    public void testPreferredURIs() {
        String uri1 = CRTestUtils.getUniqueURI();
        String uri2 = CRTestUtils.getUniqueURI();
        String uri3 = CRTestUtils.getUniqueURI();

        LinkedList<Triple> sameAsLinks = new LinkedList<Triple>();
        sameAsLinks.add(new Triple(Node.createURI(uri1), sameAsPredicate, Node.createURI(uri2)));
        sameAsLinks.add(new Triple(Node.createURI(uri2), sameAsPredicate, Node.createURI(uri3)));

        String mappedURI1;

        URIMappingImpl mappingPreferring1 = new URIMappingImpl(Collections.singleton(uri1));
        mappingPreferring1.addLinks(sameAsLinks.iterator());
        mappedURI1 = getAndTestMappedURI(uri1, mappingPreferring1);
        Assert.assertEquals(uri1, mappedURI1);

        URIMappingImpl mappingPreferring2 = new URIMappingImpl(Collections.singleton(uri2));
        mappingPreferring2.addLinks(sameAsLinks.iterator());
        mappedURI1 = getAndTestMappedURI(uri1, mappingPreferring2);
        Assert.assertEquals(uri2, mappedURI1);

        URIMappingImpl mappingPreferring3 = new URIMappingImpl(Collections.singleton(uri3));
        mappingPreferring3.addLinks(sameAsLinks.iterator());
        mappedURI1 = getAndTestMappedURI(uri1, mappingPreferring3);
        Assert.assertEquals(uri3, mappedURI1);
    }
}
