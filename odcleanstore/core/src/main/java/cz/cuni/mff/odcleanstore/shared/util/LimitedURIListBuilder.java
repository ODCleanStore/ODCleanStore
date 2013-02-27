package cz.cuni.mff.odcleanstore.shared.util;

import java.util.Iterator;

/**
 * Builder of URI lists for use in SPARQL queries.
 * Takes a collection of URIs (e.g. ["uri1", "uri2", "uri3"]) and builds a list of URIs formatted for use in
 * a SPARQL query (e.g. "<uri1>,<uri2>,<uri3>"). If the number or URIs exceed the given maximum length, the URIs
 * are divided into multiple smaller lists over which this class iterates.
 * @author Jan Michelfeit
 */
public class LimitedURIListBuilder implements Iterator<CharSequence>, Iterable<CharSequence> {
    private final int maxListLength;
    private final Iterator<String> uriCollectionIterator;

    /**
     * Constructor.
     * @param uriList list of URIs; must be absolute URIs, not prefixed names
     * @param maxListLength maximum number of URIs in one formatted list
     */
    public LimitedURIListBuilder(Iterable<String> uriList, int maxListLength) {
        this.uriCollectionIterator = uriList.iterator();
        this.maxListLength = maxListLength;
    }

    @Override
    public Iterator<CharSequence> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return uriCollectionIterator.hasNext();
    }

    @Override
    public CharSequence next() {
        StringBuilder result = new StringBuilder();
        int listLength = 0;
        while (uriCollectionIterator.hasNext() && listLength < maxListLength) {
            result.append('<');
            result.append(uriCollectionIterator.next());
            result.append('>');
            result.append(',');

            listLength++;
        }
        result.deleteCharAt(result.length() - 1);

        return result;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
