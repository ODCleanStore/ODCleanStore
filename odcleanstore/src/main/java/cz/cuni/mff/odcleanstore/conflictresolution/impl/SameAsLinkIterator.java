package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.graph.Triple;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import cz.cuni.mff.odcleanstore.vocabulary.OWL;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator over owl:sameAs triples in a collection of triples.
 * 
 * @author Jan Michelfeit
 */
class SameAsLinkIterator implements Iterator<Triple> {

    /**
     * Next sameAs triple to be returned or null if there is none.
     */
    private Triple nextSameAsTriple = null;

    /**
     * Iterator over triples in input graph.
     */
    private Iterator<? extends Triple> dataIterator;

    /**
     * Creates a new instance of iterator over owl:sameAs triples contained in the given data.
     * @param data triples to be scanned for owl:sameAs triples
     */
    public SameAsLinkIterator(Iterable<? extends Triple> data) {
        dataIterator = data.iterator();
        nextSameAsTriple = getNextSameAsTriple();
    }

    @Override
    public boolean hasNext() {
        return nextSameAsTriple != null;
    }

    @Override
    public Triple next() {
        if (nextSameAsTriple == null) {
            throw new NoSuchElementException();
        }
        Triple result = nextSameAsTriple;
        nextSameAsTriple = getNextSameAsTriple();
        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the next sameAs triple in input triples and moves the internal
     * iterator.
     * @return a next sameAs triple or null if there are none
     */
    private Triple getNextSameAsTriple() {
        while (dataIterator.hasNext()) {
            Triple next = dataIterator.next();
            assert next.getPredicate() instanceof URITripleItem;
            if (next.getPredicate().getURI().equals(OWL.sameAs)) {
                return next;
            }
        }
        return null;
    }
}
