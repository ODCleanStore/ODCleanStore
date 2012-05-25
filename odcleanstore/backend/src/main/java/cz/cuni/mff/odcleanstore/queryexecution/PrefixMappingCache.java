package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
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
/*package*/class PrefixMappingCache extends CacheHolderBase<PrefixMapping> {
    /** Lifetime of the cached value in milliseconds. */
    private static final long CACHE_LIFETIME = 10 * Utils.TIME_UNIT_60 * Utils.MILLISECONDS;

    /** Database connection settings. */
    private final SparqlEndpoint connection;

    /**
     * Create a new instance.
     * @param connection connection settings
     */
    public PrefixMappingCache(SparqlEndpoint connection) {
        super(CACHE_LIFETIME);
        this.connection = connection;
    }

    @Override
    protected PrefixMapping loadCachedValue() throws DatabaseException {
        List<RDFprefix> prefixList = null;
        prefixList = RDFPrefixesLoader.loadPrefixes(connection);
        Map<String, String> prefixMap = new HashMap<String, String>(prefixList.size());
        for (RDFprefix prefix : prefixList) {
            prefixMap.put(prefix.getPrefixId(), prefix.getNamespace());
        }
        return new PrefixMapping(prefixMap);
    }
}
