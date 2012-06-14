package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryError;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.RDFPrefixesLoader;
import cz.cuni.mff.odcleanstore.shared.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to a PrefixMapping, caching its value.
 * This class is thread-safe.
 * @author Jan Michelfeit
 */
public class PrefixMappingCache extends CacheHolderBase<PrefixMapping> {
    /** Lifetime of the cached value in milliseconds. */
    private static final long CACHE_LIFETIME = 10 * Utils.TIME_UNIT_60 * Utils.MILLISECONDS;

    /** Database connection settings. */
    private final JDBCConnectionCredentials connectionCredentials;

    /**
     * Create a new instance.
     * @param connectionCredentials connection settings
     */
    public PrefixMappingCache(JDBCConnectionCredentials connectionCredentials) {
        super(CACHE_LIFETIME);
        this.connectionCredentials = connectionCredentials;
    }

    @Override
    protected PrefixMapping loadCachedValue() throws QueryExecutionException {
        List<RDFprefix> prefixList = null;
        try {
            prefixList = RDFPrefixesLoader.loadPrefixes(connectionCredentials);
        } catch (DatabaseException e) {
            throw new QueryExecutionException(EnumQueryError.DATABASE_ERROR, e);
        }
        Map<String, String> prefixMap = new HashMap<String, String>(prefixList.size());
        for (RDFprefix prefix : prefixList) {
            prefixMap.put(prefix.getPrefixId(), prefix.getNamespace());
        }
        return new PrefixMapping(prefixMap);
    }
}
