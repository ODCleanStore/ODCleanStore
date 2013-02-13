package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * Helper class for Query Execution.
 * @author Jan Michelfeit
 */
public final class QueryExecutionHelper {

    /**
     * Builder of URI lists for use in SPARQL queries.
     * Takes a collection of URIs (e.g. ["uri1", "uri2", "uri3"]) and builds a list of URIs formatted for use in
     * a SPARQL query (e.g. "<uri1>,<uri2>,<uri3>"). If the number or URIs exceed the given maximum length, the URIs
     * are divided into multiple smaller lists over which this class iterates.
     */
    private static class LimitedURIListBuilder implements Iterator<CharSequence>, Iterable<CharSequence> {
        private final int maxListLength;
        private Iterator<String> uriCollectionIterator;

        /**
         * Constructor.
         * @param uriList list of URIs; must be absolute URIs, not prefixed names
         * @param maxListLength maximum number of URIs in one formatted list
         */
        LimitedURIListBuilder(Iterable<String> uriList, int maxListLength) {
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
            while (uriCollectionIterator.hasNext() && listLength < maxListLength)
            {
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

    /**
     * Returns a builder of URI lists for use in SPARQL queries.
     * Takes a collection of URIs (e.g. ["uri1", "uri2", "uri3"]) and builds a list of URIs formatted for use in
     * a SPARQL query (e.g. "<uri1>,<uri2>,<uri3>"). If the number or URIs exceed the given maximum length, the URIs
     * are divided into multiple smaller lists over which the returned Iterable iterates.
     *
     * @param uriList list of URIs; must be absolute URIs, not prefixed names
     * @param maxListLength maximum number of URIs in one formatted list
     * @return Iterable over formatted lists of URIs for use in SPARQL queries.
     */
    public static Iterable<CharSequence> getLimitedURIListBuilder(Iterable<String> uriList, int maxListLength) {
        return new LimitedURIListBuilder(uriList, maxListLength);
    }

    /**
     * Expands prefixed names in the given aggregation settings to full URIs.
     * @param aggregationSpec aggregation settings where property names are expanded
     * @param prefixMapping prefix mapping used for expansion
     * @return new aggregation settings
     * @throws QueryExecutionException a prefix has no defined mapping
     */
    public static AggregationSpec expandPropertyNames(AggregationSpec aggregationSpec, PrefixMapping prefixMapping)
            throws QueryExecutionException {

        if (aggregationSpec.getPropertyAggregations().isEmpty() && aggregationSpec.getPropertyMultivalue().isEmpty()) {
            return aggregationSpec;
        }
        AggregationSpec result = aggregationSpec.shallowClone();

        Map<String, EnumAggregationType> newPropertyAggregations = new TreeMap<String, EnumAggregationType>();
        for (Entry<String, EnumAggregationType> entry : aggregationSpec.getPropertyAggregations().entrySet()) {
            String property = entry.getKey();
            if (ODCSUtils.isPrefixedName(property)) {
                newPropertyAggregations.put(prefixMapping.expandPrefix(property), entry.getValue());
            } else {
                newPropertyAggregations.put(property, entry.getValue());
            }
        }
        result.setPropertyAggregations(newPropertyAggregations);

        Map<String, Boolean> newPropertyMultivalue = new TreeMap<String, Boolean>();
        for (Entry<String, Boolean> entry : aggregationSpec.getPropertyMultivalue().entrySet()) {
            String property = entry.getKey();
            if (ODCSUtils.isPrefixedName(property)) {
                newPropertyMultivalue.put(prefixMapping.expandPrefix(property), entry.getValue());
            } else {
                newPropertyMultivalue.put(property, entry.getValue());
            }
        }
        result.setPropertyMultivalue(newPropertyMultivalue);

        return result;
    }

    /** Hide constructor for a utility class. */
    private QueryExecutionHelper() {
    }
}
