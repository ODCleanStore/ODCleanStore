package cz.cuni.mff.odcleanstore.conflictresolution;

/**
 * Type of strategy to use when an aggregation cannot be applied to a value.
 *
 * @author Jan Michelfeit
 */
public enum AggregationErrorStrategy {
    /** Discard value. */
    IGNORE,
    
    /** Return value without aggregation. */
    RETURN_ALL
}
