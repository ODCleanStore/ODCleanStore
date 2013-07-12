package cz.cuni.mff.odcleanstore.conflictresolution;

/**
 * Type of strategy to use when an aggregation by resolution function cannot 
 * be applied to a value (e.g. AVG applied to a string).
 * 
 * @author Jan Michelfeit
 */
public enum EnumAggregationErrorStrategy {
    /** Discard value. */
    IGNORE,

    /** Return value without aggregation. */
    RETURN_ALL
}
