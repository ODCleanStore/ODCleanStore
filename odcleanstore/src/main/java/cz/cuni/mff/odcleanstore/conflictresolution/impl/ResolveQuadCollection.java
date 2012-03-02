package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.QuadGraph;
import cz.cuni.mff.odcleanstore.graph.Triple;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;
import cz.cuni.mff.odcleanstore.shared.TripleItemComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Quad container that provides access to clusters of conflicting quads with
 * support for URI mapping.
 * 
 * @author Jan Michelfeit
 */
class ResolveQuadCollection {
    /**
     * A comparator used to sort quads in conflict clusters.
     */
    private static final SubjectPropertyComparator CONFLICT_COMPARATOR;

    /**
     * Container of quads.
     */
    private ArrayList<Quad> quadList = new ArrayList<Quad>();

    /**
     * Indicates whether {@link #quadList} is sorted.
     */
    private boolean quadListSorted = false;

    /**
     * Comparator of {@link Triple Triples} comparing firstly by subject and
     * secondly by property (object is ignored).
     */
    private static class SubjectPropertyComparator implements Comparator<Triple> {
        @Override
        public int compare(Triple o1, Triple o2) {
            int subjectComparison = TripleItemComparator.compare(o1.getSubject(), o2.getSubject());
            if (subjectComparison != 0) {
                return subjectComparison;
            } else {
                return TripleItemComparator.compare(o1.getPredicate(), o2.getPredicate());
            }
        }
    };

    /**
     * Iterator over clusters of conflicting triples (i.e. those having the
     * same subject and predicate) as collections of quads.
     * 
     * The iterator becomes invalid whenever the quad collection contained in
     * {@linkplain ResolveQuadCollection the outer class} changes.
     */
    private class ConflictingQuadsIterator implements Iterator<Collection<Quad>> {
        /**
         * Iterator over the sorted quad collection in the outer class.
         * Between calls to {@link #hasNext()} points before the first quad from
         * the next conflicting cluster of quads.
         */
        private ListIterator<Quad> quadIterator;

        /** Creates a new iterator instance. */
        public ConflictingQuadsIterator() {
            sortQuadList();
            quadIterator = quadList.listIterator();
        }

        @Override
        public boolean hasNext() {
            return quadIterator.hasNext();
        }

        @Override
        public Collection<Quad> next() {
            int fromIndex = quadIterator.nextIndex();
            Quad first = quadIterator.next();

            while (quadIterator.hasNext()) {
                Quad next = quadIterator.next();

                if (!next.getSubject().equals(first.getSubject())
                        || !next.getPredicate().equals(first.getPredicate())) {
                    // We reached the next cluster of conflicting quads
                    // -> return quadIterator so that it points before the first
                    // quad from the next cluster
                    quadIterator.previous();
                    break;
                }
            }
            return quadList.subList(fromIndex, quadIterator.nextIndex());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Call to remove() is not supported.");
        }
    }

    /**
     * Initialize comparator.
     */
    static {
        CONFLICT_COMPARATOR = new SubjectPropertyComparator();
    }

    /**
     * Adds quads to the collection.
     * @param quads quads to add
     */
    public void addQuads(QuadGraph quads) {
        quadList.ensureCapacity(quadList.size() + quads.size());
        for (Quad quad : quads) {
            quadList.add(quad);
        }
        quadListSorted = false;
    }

    /**
     * Change all resource URIs in contained quads according to
     * an {@link URIMapping}.
     * @param mapping a mapping of URIs to apply
     */
    public void applyMapping(URIMapping mapping) {
        int quadCount = quadList.size();
        for (int i = 0; i < quadCount; i++) {
            Quad quad = quadList.get(i);

            TripleItem subject = quad.getSubject();
            TripleItem subjectMapping = mapTripleItem(subject, mapping);
            TripleItem predicate = quad.getPredicate();
            TripleItem predicateMapping = mapTripleItem(predicate, mapping);
            TripleItem object = quad.getObject();
            TripleItem objectMapping = mapTripleItem(object, mapping);

            // Intentionally !=
            if (subject != subjectMapping
                    || predicate != predicateMapping
                    || object != objectMapping) {
                Quad newQuad = new Quad(subjectMapping, predicateMapping,
                        objectMapping, quad.getNamedGraph());
                quadList.set(i, newQuad);
                quadListSorted = false;
            }
        }
    }

    /**
     * Returns an iterator over clusters of conflicting quads.
     * Each cluster of conflicting quads (i.e. those having the same subject
     * and predicate) is represented by a quad collection. Any change to quads
     * contained in this instance invalidates the iterator.
     * @return iterator over nonempty collections of conflicting quads
     */
    public Iterator<Collection<Quad>> listConflictingQuads() {
        return new ConflictingQuadsIterator();
    }

    /**
     * Sort containted quads using {@link SubjectPropertyComparator}.
     */
    private void sortQuadList() {
        if (!quadListSorted) {
            Collections.sort(quadList, CONFLICT_COMPARATOR);
            quadListSorted = true;
        }
    }

    /**
     * If mapping contains an URI to map for the passed URITripleItem, returns
     * an URITripleItem with the mapped URI, otherwise returns tripleItem.
     * @param tripleItem a TripleItem to apply mapping to
     * @param mapping an URI mapping to apply
     * @return tripleItem with applied URI mapping
     */
    private TripleItem mapTripleItem(TripleItem tripleItem, URIMapping mapping) {
        if (tripleItem instanceof URITripleItem) {
            String mappedURI = mapping.mapURI(tripleItem.getURI());
            if (mappedURI != null) {
                return new URITripleItem(mappedURI);
            }
        }
        return tripleItem;
    }
}
