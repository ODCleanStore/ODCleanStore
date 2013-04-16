package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

/**
 * Comparator of quads used by {@link cz.cuni.mff.odcleanstore.conflictresolution.aggregation.BestSelectedAggregation}.
 * @author Jan Michelfeit
 */
public interface AggregationComparator {
    /**
     * Returns true iff the comparison is applicable to the given quad.
     * @param quad a quad
     * @return true iff the comparison is applicable to the given quad
     */
    boolean isAggregable(Statement quad);

    /**
     * Compares two quads for order.
     * Must be called only with arguments checked by {@link #isAggregable(Quad)} first.
     * @param quad1 first compared quad; {@link #isAggregable(Quad)} must return true for the quad
     * @param quad2 second compared quad; {@link #isAggregable(Quad)} must return true for the quad
     * @param metadata metadata for named graphs containing the quads being resolved
     * @return a negative integer, zero, or a positive integer as the first argument
     *      is less than, equal to, or greater than the second
     */
    int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata);
}
