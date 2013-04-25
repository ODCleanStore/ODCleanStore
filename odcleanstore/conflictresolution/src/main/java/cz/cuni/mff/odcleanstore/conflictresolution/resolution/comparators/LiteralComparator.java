package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * Comparator of quads used by {@link cz.cuni.mff.odcleanstore.crold.aggregation.BestSelectedAggregation}.
 * @author Jan Michelfeit
 */
public interface LiteralComparator {
    /**
     * Returns true iff the comparison is applicable to the given quad.
     * @param crContext TODO
     * @param quad a quad
     * @return true iff the comparison is applicable to the given quad
     */
    boolean accept(Value object, CRContext crContext);

    /**
     * Compares two quads for order.
     * Must be called only with arguments checked by {@link #accept(Statement, CRContext)} first. // TODO: ?
     * Must be null proof
     * @param quad1 first compared quad; {@link #accept(Quad, CRContext)} must return true for the quad
     * @param quad2 second compared quad; {@link #accept(Quad, CRContext)} must return true for the quad
     * @param metadata metadata for named graphs containing the quads being resolved
     * @return a negative integer, zero, or a positive integer as the first argument
     *      is less than, equal to, or greater than the second
     */
    int compare(Value object1, Value object2);
}
