package cz.cuni.mff.odcleanstore.conflictresolution.exceptions;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationType;

/**
 * Exception to throw when an attempt to create an AggregationMethod of
 * an unknown type is made.
 * @see EnumAggregationType
 *
 * @author Jan Michelfeit
 */
public class AggregationNotImplementedException extends ConflictResolutionException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception for the given not implemented aggregation type.
     * @param aggregationType an aggregation method that is not implemented yet
     */
    public AggregationNotImplementedException(EnumAggregationType aggregationType) {
        super("Aggregation type \"" + aggregationType.toString() + "\" is not implmeneted");
    }
}