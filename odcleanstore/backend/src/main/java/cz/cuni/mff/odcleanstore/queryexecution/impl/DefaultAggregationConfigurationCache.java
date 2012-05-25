package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;
import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryError;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.Utils;

/**
 * Provides access to default aggregation settings, caching the value.
 * This class is thread-safe.
 * @author Jan Michelfeit
 */
public class DefaultAggregationConfigurationCache extends CacheHolderBase<AggregationSpec> {
    /** Lifetime of the cached value in milliseconds. */
    private static final long CACHE_LIFETIME = 5 * Utils.TIME_UNIT_60 * Utils.MILLISECONDS;

    /** Database connection settings. */
    private final ConnectionCredentials connection;

    /** Prefix mappings. */
    private final PrefixMappingCache prefixMappingCache;

    /**
     * Create a new instance.
     * @param connection connection settings
     * @param prefixMappingCache cached prefix mapping
     */
    public DefaultAggregationConfigurationCache(
            ConnectionCredentials connection, PrefixMappingCache prefixMappingCache) {

        super(CACHE_LIFETIME);
        this.connection = connection;
        this.prefixMappingCache = prefixMappingCache;
    }

    @Override
    protected AggregationSpec loadCachedValue() throws QueryExecutionException {
        try {
            AggregationSpec defaultSettings = new QueryExecutionConfigLoader(connection).getDefaultSettings();
            return QueryExecutionHelper.expandPropertyNames(defaultSettings, prefixMappingCache.getCachedValue());
        } catch (DatabaseException e) {
            throw new QueryExecutionException(EnumQueryError.DATABASE_ERROR, e);
        }
    }
}
