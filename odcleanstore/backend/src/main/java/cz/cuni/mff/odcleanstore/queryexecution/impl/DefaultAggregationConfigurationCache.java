package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryError;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.ODCSErrorCodes;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

/**
 * Provides access to default aggregation settings, caching the value.
 * This class is thread-safe.
 * @author Jan Michelfeit
 */
public class DefaultAggregationConfigurationCache extends CacheHolderBase<ConflictResolutionPolicy> {
    /** Lifetime of the cached value in milliseconds. */
    private static final long CACHE_LIFETIME = 5 * ODCSUtils.TIME_UNIT_60 * ODCSUtils.MILLISECONDS;

    /** Database connection settings. */
    private final JDBCConnectionCredentials connectionCredentials;

    /** Prefix mappings. */
    private final PrefixMappingCache prefixMappingCache;

    /**
     * Create a new instance.
     * @param connectionCredentials connection settings
     * @param prefixMappingCache cached prefix mapping
     */
    public DefaultAggregationConfigurationCache(
            JDBCConnectionCredentials connectionCredentials, PrefixMappingCache prefixMappingCache) {

        super(CACHE_LIFETIME);
        this.connectionCredentials = connectionCredentials;
        this.prefixMappingCache = prefixMappingCache;
    }

    @Override
    protected ConflictResolutionPolicy loadCachedValue() throws QueryExecutionException {
        try {
            QueryExecutionConfigLoader configLoader = new QueryExecutionConfigLoader(connectionCredentials);
            return configLoader.getDefaultSettings(prefixMappingCache.getCachedValue());
        } catch (DatabaseException e) {
            throw new QueryExecutionException(
                    EnumQueryError.DATABASE_ERROR,
                    ODCSErrorCodes.QE_DEFAULT_CONFIG_DB_ERR,
                    "Database error",
                    e);
        } catch (QueryExecutionException e) {
            throw new QueryExecutionException(
                    EnumQueryError.DEFAULT_AGGREGATION_SETTINGS_INVALID,
                    ODCSErrorCodes.QE_DEFAULT_CONFIG_PREFIX_ERR,
                    "Unkown prefix used in default aggregation settings",
                    e);
        }

    }
}
