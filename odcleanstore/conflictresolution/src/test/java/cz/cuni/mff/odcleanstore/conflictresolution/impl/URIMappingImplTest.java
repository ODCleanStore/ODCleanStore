package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.URIMapping;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 *
 * @author Jan Michelfeit
 */
public class URIMappingImplTest {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    @BeforeClass
    public static void setUpClass() throws Exception {
        CRTestUtils.resetURICounter();
    }

    private String getAndTestMappedURI(String uri, URIMapping mapping) {
        URI mappedURI = mapping.mapURI(VALUE_FACTORY.createURI(uri));
        String mappedByNode = mappedURI.stringValue();
        String canonicalURI = mapping.getCanonicalURI(uri);
        Assert.assertEquals(mappedByNode, canonicalURI);
        return canonicalURI;
    }

    @Test
    public void testEmptyMapping() {
        URIMappingImpl instance = new URIMappingImpl();
        URI uri = VALUE_FACTORY.createURI(CRTestUtils.getUniqueURIString());
        URI expResult = uri;
        URI result = instance.mapURI(uri);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testNonEmptyMapping1() {
        String uri1 = CRTestUtils.getUniqueURIString();
        String uri2 = CRTestUtils.getUniqueURIString();
        String uri3 = CRTestUtils.getUniqueURIString();
        String uri4 = CRTestUtils.getUniqueURIString();

        LinkedList<Statement> sameAsLinks = new LinkedList<Statement>();
        sameAsLinks.add(CRTestUtils.createStatement(
                uri1,
                OWL.sameAs,
                uri2));
        sameAsLinks.add(CRTestUtils.createStatement(
                uri2,
                OWL.sameAs,
                uri3));

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
        String rootURI = CRTestUtils.getUniqueURIString();
        String uri1 = CRTestUtils.getUniqueURIString();
        String uri2 = CRTestUtils.getUniqueURIString();
        String uri3 = CRTestUtils.getUniqueURIString();

        LinkedList<Statement> sameAsLinks = new LinkedList<Statement>();
        sameAsLinks.add(CRTestUtils.createStatement(
                rootURI,
                OWL.sameAs,
                uri1));
        sameAsLinks.add(CRTestUtils.createStatement(
                rootURI,
                OWL.sameAs,
                uri2));
        sameAsLinks.add(CRTestUtils.createStatement(
                rootURI,
                OWL.sameAs,
                uri3));

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
        String uri1 = CRTestUtils.getUniqueURIString();
        String uri2 = CRTestUtils.getUniqueURIString();
        String uri3 = CRTestUtils.getUniqueURIString();

        LinkedList<Statement> sameAsLinks = new LinkedList<Statement>();
        sameAsLinks.add(CRTestUtils.createStatement(
                uri1,
                OWL.sameAs,
                uri2));
        sameAsLinks.add(CRTestUtils.createStatement(
                uri2,
                OWL.sameAs,
                uri3));
        sameAsLinks.add(CRTestUtils.createStatement(
                uri3,
                OWL.sameAs,
                uri1));

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
        String uri1 = CRTestUtils.getUniqueURIString();
        String uri2 = CRTestUtils.getUniqueURIString();
        String uri3 = CRTestUtils.getUniqueURIString();

        LinkedList<Statement> sameAsLinks = new LinkedList<Statement>();
        sameAsLinks.add(CRTestUtils.createStatement(
                uri1,
                OWL.sameAs,
                uri2));
        sameAsLinks.add(CRTestUtils.createStatement(
                uri2,
                OWL.sameAs,
                uri3));

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
