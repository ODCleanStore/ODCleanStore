package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.shared.QuadComparator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Quad container that provides access to clusters of conflicting quads with
 * support for URI mapping.
 *
 * @author Jan Michelfeit
 */
/*package*/class ResolveQuadCollection {
    /**
     * A comparator used to sort quads in conflict clusters.
     */
    private static final Comparator<Quad> CONFLICT_COMPARATOR = new QuadComparator();

    /**
     * Container of quads.
     */
    private ArrayList<Quad> quadList = new ArrayList<Quad>();

    /**
     * Indicates whether {@link #quadList} is sorted.
     */
    private boolean quadListSorted = false;

    /**
     * Iterator over clusters of conflicting triples (i.e. those having the
     * same subject and predicate) as collections of quads.
     *
     * The iterator becomes invalid whenever the quad collection contained in
     * {@linkplain ResolveQuadCollection the outer class} changes.
     * @see Node#sameValueAs(Object)
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
            if (!quadListSorted) {
                throw new IllegalStateException("Iterator invalidated.");
            }
            int fromIndex = quadIterator.nextIndex();
            Quad first = quadIterator.next();

            while (quadIterator.hasNext()) {
                Quad next = quadIterator.next();

                if (!next.getSubject().sameValueAs(first.getSubject())
                        || !next.getPredicate().sameValueAs(first.getPredicate())) {
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
     * Adds quads to the collection.
     * Invalidates iterator obtained by {@link #listConflictingQuads()}
     * @param quads quads to add
     */
    public void addQuads(Collection<Quad> quads) {
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

            Node subject = quad.getSubject();
            Node subjectMapping = mapURINode(subject, mapping);
            Node predicate = quad.getPredicate();
            Node predicateMapping = mapURINode(predicate, mapping);
            Node object = quad.getObject();
            Node objectMapping = mapURINode(object, mapping);

            // Intentionally !=
            if (subject != subjectMapping
                    || predicate != predicateMapping
                    || object != objectMapping) {
                Quad newQuad = new Quad(
                        quad.getGraphName(),
                        subjectMapping,
                        predicateMapping,
                        objectMapping);
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
     * Sort contained quads using {@link SubjectPropertyComparator} and remove duplicates.
     */
    private void sortQuadList() {
        if (!quadListSorted) {
            // Sort quads - this is what Java sort does internally anyway
            Quad[] quadArray = quadList.toArray(new Quad[quadList.size()]);
            Arrays.sort(quadArray, CONFLICT_COMPARATOR);

            // Copy to the sorted array to the original list, leaving out duplicates
            ListIterator<Quad> listIt = quadList.listIterator();
            int i;
            for (i = 0; i < quadArray.length - 1; i++) {
                if (!quadArray[i].equals(quadArray[i+1])) {
                    listIt.next();
                    listIt.set(quadArray[i]);
                }
            }
            if (i < quadArray.length) {
                // Add the last element
                listIt.next();
                listIt.set(quadArray[i]);
            }

            // Remove the excess items in reverse order (to avoid shifting the rest of the list)
            int newSize = listIt.nextIndex();
            for (int j = quadList.size() - 1; j >= newSize; j--) {
                quadList.remove(j);
            }

            quadListSorted = true;
        }
    }

    /**
     * If mapping contains an URI to map for the passed {@link com.hp.hpl.jena.graph.Node_URI},
     * returns a Node_URI with the mapped URI, otherwise returns node.
     * @param node a Node to apply mapping to
     * @param mapping an URI mapping to apply
     * @return node with applied URI mapping
     */
    private Node mapURINode(Node node, URIMapping mapping) {
        if (node.isURI()) {
            Node mappedURI = mapping.mapURI(node);
            if (mappedURI != null) {
                return mappedURI;
            }
        }
        return node;
    }
}
