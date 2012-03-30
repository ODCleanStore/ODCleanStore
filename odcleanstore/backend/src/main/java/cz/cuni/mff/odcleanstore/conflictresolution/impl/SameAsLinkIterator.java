package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over owl:sameAs quads in a collection of quads.
 *
 * @author Jan Michelfeit
 */
/*package*/class SameAsLinkIterator implements Iterator<Triple> {

    /**
     * Next sameAs quad to be returned or null if there is none.
     */
    private Quad nextSameAsQuad = null;

    /**
     * Iterator over quads in input graph.
     */
    private Iterator<? extends Quad> dataIterator;

    /**
     * Creates a new instance of iterator over owl:sameAs quads contained in the given data.
     * @param data quads to be scanned for owl:sameAs quads
     */
    public SameAsLinkIterator(Iterable<? extends Quad> data) {
        dataIterator = data.iterator();
        nextSameAsQuad = getNextSameAsQuad();
    }

    @Override
    public boolean hasNext() {
        return nextSameAsQuad != null;
    }

    @Override
    public Triple next() {
        if (nextSameAsQuad == null) {
            throw new NoSuchElementException();
        }
        Quad result = nextSameAsQuad;
        nextSameAsQuad = getNextSameAsQuad();
        return result.getTriple();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next sameAs quad in input quads and moves the internal iterator.
     * @return a next sameAs quad or null if there are none
     */
    private Quad getNextSameAsQuad() {
        while (dataIterator.hasNext()) {
            Quad next = dataIterator.next();
            if (next.getPredicate().hasURI(OWL.sameAs)) {
                return next;
            }
        }
        return null;
    }
}
