package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import de.fuberlin.wiwiss.ng4j.Quad;

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
        LinkedList<Quad> triples = new LinkedList<Quad>();
        String uri1 = TestUtils.getUniqueURI();
        String uri2 = TestUtils.getUniqueURI();

        triples.add(TestUtils.createQuad(uri1, OWL.sameAs, TestUtils.getUniqueURI()));
        triples.add(TestUtils.createQuad());
        triples.add(TestUtils.createQuad(uri2, OWL.sameAs, TestUtils.getUniqueURI()));

        SameAsLinkIterator sameAsIterator = new SameAsLinkIterator(triples);
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri1, sameAsIterator.next().getSubject().getURI());
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri2, sameAsIterator.next().getSubject().getURI());
        Assert.assertFalse(sameAsIterator.hasNext());
    }
}
