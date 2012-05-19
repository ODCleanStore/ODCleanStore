package cz.cuni.mff.odcleanstore.conflictresolution;

/**
 * Type of aggregation method.
 *
 * @author Jan Michelfeit
 */
public enum EnumAggregationType {
    /** Select any single value. */
    ANY,

    /** Select all values. */
    ALL,

    /** Select value with highest quality. */
    BEST,

    /** Select newest value. */
    LATEST,

    /** Select value with the highest calculated quality of its source. */
    BEST_SOURCE,

    /** Select C best values, C is cardinality of the respective property. */
    TOPC,

    /** Select maximum. */
    MAX,

    /** Select minimum. */
    MIN,

    /** Compute average. */
    AVG,

    /** Select median. */
    MEDIAN,

    /** Return all values concatenated. */
    CONCAT,

    /** Select shortest value. */
    SHORTEST,

    /** Select longest value. */
    LONGEST,

    /** No aggregation (similar to ALL without implicit Conflict Resolution. */
    NONE
}