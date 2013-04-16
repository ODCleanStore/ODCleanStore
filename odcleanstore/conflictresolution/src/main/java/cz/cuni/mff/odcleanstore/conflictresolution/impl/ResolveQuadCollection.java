package cz.cuni.mff.odcleanstore.conflictresolution.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

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
    private static final Comparator<Statement> CONFLICT_COMPARATOR = new StatementComparator();
    
    /**
     * Factory for {@link Value values}.
     */
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();

    /**
     * Container of quads.
     */
    private final ArrayList<Statement> quadList = new ArrayList<Statement>();

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
     * @see ConflictResolverImpl#crSameValues(Value, Value)
     */
    private class ConflictingQuadsIterator implements Iterator<Collection<Statement>> {
        /**
         * Iterator over the sorted quad collection in the outer class.
         * Between calls to {@link #hasNext()} points before the first quad from
         * the next conflicting cluster of quads.
         */
        private final ListIterator<Statement> quadIterator;

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
        public Collection<Statement> next() {
            if (!quadListSorted) {
                throw new IllegalStateException("Iterator invalidated.");
            }
            int fromIndex = quadIterator.nextIndex();
            Statement first = quadIterator.next();

            while (quadIterator.hasNext()) {
                Statement next = quadIterator.next();

                if (!ConflictResolverImpl.crSameValues(next.getSubject(), first.getSubject())
                        || !ConflictResolverImpl.crSameValues(next.getPredicate(), first.getPredicate())) {
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
    public void addQuads(Collection<Statement> quads) {
        quadList.ensureCapacity(quadList.size() + quads.size());
        for (Statement quad : quads) {
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
            Statement quad = quadList.get(i);

            Resource subject = quad.getSubject();
            Resource subjectMapping = (Resource) mapURINode(subject, mapping);
            URI predicate = quad.getPredicate();
            URI predicateMapping = (URI) mapURINode(predicate, mapping);
            Value object = quad.getObject();
            Value objectMapping = mapURINode(object, mapping);

            // Intentionally !=
            if (subject != subjectMapping
                    || predicate != predicateMapping
                    || object != objectMapping) {
                Statement newQuad = VALUE_FACTORY.createStatement(
                        subjectMapping,
                        predicateMapping,
                        objectMapping,
                        quad.getContext());
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
    public Iterator<Collection<Statement>> listConflictingQuads() {
        return new ConflictingQuadsIterator();
    }

    /**
     * Sort contained quads using {@link SubjectPropertyComparator} and remove duplicates.
     */
    private void sortQuadList() {
        if (!quadListSorted) {
            // Sort quads - this is what Java sort does internally anyway
            Statement[] quadArray = quadList.toArray(new Statement[quadList.size()]);
            Arrays.sort(quadArray, CONFLICT_COMPARATOR);

            // Copy to the sorted array to the original list, leaving out duplicates
            ListIterator<Statement> listIt = quadList.listIterator();
            int i;
            for (i = 0; i < quadArray.length - 1; i++) {
                Statement current = quadArray[i];
                Statement next = quadArray[i + 1];
                if (!current.equals(next) || !ODCSUtils.nullProofEquals(current.getContext(), next.getContext())) {
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
     * If mapping contains an URI to map for the passed {@link URI} 
     * returns a {@link URI} with the mapped URI, otherwise returns <code>value</code>.
     * @param value a {@link Value} to apply mapping to
     * @param mapping an URI mapping to apply
     * @return node with applied URI mapping
     */
    private Value mapURINode(Value value, URIMapping mapping) {
        if (value instanceof URI) {
            URI mappedURI = mapping.mapURI((URI) value);
            if (mappedURI != null) {
                return mappedURI;
            }
        }
        return value;
    }
}
