package cz.cuni.mff.odcleanstore.queryexecution;

/**
 * Type of error during query execution.
 * @author Jan Michelfeit
 */
public enum EnumQueryError {
    /** Query is too long. */
    QUERY_TOO_LONG,

    /** Query has an invalid format. */
    INVALID_QUERY_FORMAT,

    /** Default aggregation settings are invalid. */
    DEFAULT_AGGREGATION_SETTINGS_INVALID,

    /** Aggregation settings are invalid. */
    AGGREGATION_SETTINGS_INVALID,

    /** Query execution settings are invalid. */
    QUERY_EXECUTION_SETTINGS_INVALID,

    /** Error when accessing the database. */
    DATABASE_ERROR,

    /** Error during conflict resolution. */
    CONFLICT_RESOLUTION_ERROR,

    /** Unknown namespace prefix. */
    UNKNOWN_PREFIX,

    /** Unknown conflict resolution function. */
    UNKNOWN_RESOLUTION_FUNCTION
}
