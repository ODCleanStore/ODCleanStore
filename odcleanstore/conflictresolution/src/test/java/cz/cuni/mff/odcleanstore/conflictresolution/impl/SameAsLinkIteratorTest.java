package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRTestUtils;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

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
        LinkedList<Statement> triples = new LinkedList<Statement>();
        String uri1 = CRTestUtils.getUniqueURI();
        String uri2 = CRTestUtils.getUniqueURI();

        triples.add(CRTestUtils.createStatement(uri1, OWL.sameAs, CRTestUtils.getUniqueURI()));
        triples.add(CRTestUtils.createStatement());
        triples.add(CRTestUtils.createStatement(uri2, OWL.sameAs, CRTestUtils.getUniqueURI()));

        SameAsLinkIterator sameAsIterator = new SameAsLinkIterator(triples);
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri1, sameAsIterator.next().getSubject().stringValue());
        Assert.assertTrue(sameAsIterator.hasNext());
        Assert.assertEquals(uri2, sameAsIterator.next().getSubject().stringValue());
        Assert.assertFalse(sameAsIterator.hasNext());
    }
}
