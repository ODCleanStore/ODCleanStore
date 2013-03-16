package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 *
 * @author Jan Michelfeit
 */
public class SameAsLinkIteratorTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        CRTestUtils.resetURICounter();
    }

    @Test
    public void testIterator() {
        LinkedList<Quad> triples = new LinkedList<Quad>();
        String uri1 = CRTestUtils.getUniqueURI();
        String uri2 = CRTestUtils.getUniqueURI();

        triples.add(CRTestUtils.createQuad(uri1, OWL.sameAs, CRTestUtils.getUniqueURI()));
        triples.add(CRTestUtils.createQuad());
        triples.add(CRTestUtils.createQuad(uri2, OWL.sameAs, CRTestUtils.getUniqueURI()));

        SameAsLinkIterator sameAsIterator = new SameAsLinkIterator(triples);
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri1, sameAsIterator.next().getSubject().getURI());
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri2, sameAsIterator.next().getSubject().getURI());
        Assert.assertFalse(sameAsIterator.hasNext());
    }
}
