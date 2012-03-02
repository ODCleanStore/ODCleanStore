package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationType;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

/**
 * Exception to throw when an attempt to create an AggregationMethod of
 * an unknown type is made.
 * @see AggregationType
 * 
 * @author Jan Michelfeit
 */
public class AggregationNotImplementedException extends ODCleanStoreException {
    /**
     * Constructs a new exception for the given not implemented aggregation type.
     * @param aggregationType an aggregation method that is not implemented yet
     */
    public AggregationNotImplementedException(AggregationType aggregationType) {
        super("Aggregation type \"" + aggregationType.toString() + "\" is not implmeneted");
    }
}
