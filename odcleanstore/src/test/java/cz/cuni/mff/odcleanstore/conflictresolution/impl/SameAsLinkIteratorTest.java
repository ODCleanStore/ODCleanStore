package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.graph.Triple;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedList;

/**
 * 
 * @author Jan Michelfeit
 */
public class SameAsLinkIteratorTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        TestUtils.resetURICounter();
    }

    @Test
    public void testIterator() {
        LinkedList<Triple> triples = new LinkedList<Triple>();
        String uri1 = TestUtils.getUniqueURI();
        String uri2 = TestUtils.getUniqueURI();

        triples.add(TestUtils.createTriple(uri1, OWL.sameAs, TestUtils.getUniqueURI()));
        triples.add(TestUtils.createTriple());
        triples.add(TestUtils.createTriple(uri2, OWL.sameAs, TestUtils.getUniqueURI()));

        SameAsLinkIterator sameAsIterator = new SameAsLinkIterator(triples);
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri1, sameAsIterator.next().getSubject().getURI());
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri2, sameAsIterator.next().getSubject().getURI());
        Assert.assertFalse(sameAsIterator.hasNext());
    }
}
