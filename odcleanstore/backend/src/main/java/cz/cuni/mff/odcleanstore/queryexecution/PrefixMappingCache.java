package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.data.RDFprefix;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.shared.RDFPrefixesLoader;
import cz.cuni.mff.odcleanstore.shared.Utils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to a PrefixMapping, caching its value for the given lifetime.
 * This class is thread-safe.
 * @author Jan Michelfeit
 */
/*package*/class PrefixMappingCache {
    /** Lifetime of the cached PrefixMapping in milliseconds. */
    public static final long CACHE_LIFETIME = 10 * Utils.TIME_UNIT_60 * Utils.MILLISECONDS; // 10 min

    /** Database connection settings. */
    private final SparqlEndpoint connection;

    /** The cached PrefixMapping. */
    private PrefixMapping prefixMapping;

    /** Last time the cached value was refreshed from the database. -1 means never. */
    private volatile long lastRefreshTime = -1;

    /**
     * Create a new instance.
     * @param connection connection settings
     */
    public PrefixMappingCache(SparqlEndpoint connection) {
        this.connection = connection;
    }

    /**
     * Return the prefix mapping.
     * @return instance of {@link PrefixMapping}
     * @throws ODCleanStoreException database error
     */
    public PrefixMapping getPrefixMapping() throws ODCleanStoreException {
        assert CACHE_LIFETIME > 0; // avoids prefixMapping being null
        if (System.currentTimeMillis() - lastRefreshTime > CACHE_LIFETIME) {
            // CHECKSTYLE:OFF
            synchronized (this) {
                if (System.currentTimeMillis() - lastRefreshTime > CACHE_LIFETIME) {
                    refreshMapping();
                }
            }
            // CHECKSTYLE:ON
        }
        return prefixMapping;
    }

    /**
     * Loads the current prefix mappings from the database and updates last refresh time.
     * @throws ODCleanStoreException database error
     */
    private synchronized void refreshMapping() throws ODCleanStoreException {
        List<RDFprefix> prefixList = null;
        try {
            prefixList = RDFPrefixesLoader.loadPrefixes(connection);
        } catch (SQLException e) {
            throw new ODCleanStoreException(e);
        }
        Map<String, String> prefixMap = new HashMap<String, String>(prefixList.size());
        for (RDFprefix prefix : prefixList) {
            prefixMap.put(prefix.getPrefixId(), prefix.getNamespace());
        }
        prefixMapping = new PrefixMapping(prefixMap);
        lastRefreshTime = System.currentTimeMillis();
    }
}
