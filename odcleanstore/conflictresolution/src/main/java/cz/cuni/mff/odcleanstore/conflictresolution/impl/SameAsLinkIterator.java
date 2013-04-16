package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.vocabulary.OWL;

/**
 * Iterator over owl:sameAs quads in a collection of quads.
 *
 * @author Jan Michelfeit
 */
/*package*/class SameAsLinkIterator implements Iterator<Statement> {

    /**
     * Next sameAs quad to be returned or null if there is none.
     */
    private Statement nextSameAsQuad = null;

    /**
     * Iterator over quads in input graph.
     */
    private final Iterator<? extends Statement> dataIterator;

    /**
     * Creates a new instance of iterator over owl:sameAs quads contained in the given data.
     * @param data quads to be scanned for owl:sameAs quads
     */
    public SameAsLinkIterator(Iterable<? extends Statement> data) {
        dataIterator = data.iterator();
        nextSameAsQuad = getNextSameAsQuad();
    }

    @Override
    public boolean hasNext() {
        return nextSameAsQuad != null;
    }

    @Override
    public Statement next() {
        if (nextSameAsQuad == null) {
            throw new NoSuchElementException();
        }
        Statement result = nextSameAsQuad;
        nextSameAsQuad = getNextSameAsQuad();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next sameAs quad in input quads and moves the internal iterator.
     * @return a next sameAs quad or null if there are none
     */
    private Statement getNextSameAsQuad() {
        while (dataIterator.hasNext()) {
            Statement next = dataIterator.next();
            if (OWL.sameAs.equals(next.getPredicate().stringValue())) {
                return next;
            }
        }
        return null;
    }
}
