package cz.cuni.mff.odcleanstore.queryexecution;

/**
 * Type of error during query execution.
 * @author Jan Michelfeit
 */
public enum EnumQueryError {
    QUERY_TOO_LONG,
    INVALID_QUERY_FORMAT,
    DEFAULT_AGGREGATION_SETTINGS_INVALID,
    AGGREGATION_SETTINGS_INVALID,
    DATABASE_ERROR,
    CONFLICT_RESOLUTION_ERROR,
    UNKNOWN_PREFIX
}
