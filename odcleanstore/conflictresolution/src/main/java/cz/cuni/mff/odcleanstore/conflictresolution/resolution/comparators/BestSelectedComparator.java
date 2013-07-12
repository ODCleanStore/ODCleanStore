package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * Comparator of values used by {@link cz.cuni.mff.odcleanstore.crold.aggregation.BestSelectedAggregation}.
 * @param <T> type of compared values
 * @author Jan Michelfeit
 */
public interface BestSelectedComparator<T> {
    /**
     * Returns true iff the comparison is applicable to the given value.
     * @param value value to be compared
     * @param crContext context object for the conflict resolution (containing resolution settings, additional metadata etc.)
     * @return true iff the comparison is applicable to the given value
     */
    boolean accept(T value, CRContext crContext);

    /**
     * Compares two values.
     * Should be called only with arguments previously checked by {@link #accept(Object, CRContext))}. 
     * Must be null proof for compared values.
     * @param o1 first compared value
     * @param o2 second compared value
     * @param crContext context object for the conflict resolution (containing resolution settings, additional metadata etc.)
     * @return a negative integer, zero, or a positive integer as the first argument
     *      is less than, equal to, or greater than the second
     */
    int compare(T o1, T o2, CRContext crContext);
}
