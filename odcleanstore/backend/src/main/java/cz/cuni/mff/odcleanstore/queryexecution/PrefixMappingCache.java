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

/*package*/class PrefixMappingCache {
    public static final long CACHE_LIFETIME = 10 * Utils.TIME_UNIT_60 * Utils.MILLISECONDS; // 10 min

    private SparqlEndpoint connection;
    private PrefixMapping prefixMapping;
    private long lastRefreshTime = -1;

    public PrefixMappingCache(SparqlEndpoint connection) {
        this.connection = connection;
    }

    public PrefixMapping getPrefixMapping() throws ODCleanStoreException {
        assert CACHE_LIFETIME > 0;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRefreshTime > CACHE_LIFETIME) {
            prefixMapping = loadMapping();
            lastRefreshTime = currentTime;
        }
        return prefixMapping;
    }

    private PrefixMapping loadMapping() throws ODCleanStoreException {
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
        return new PrefixMapping(prefixMap);
    }
}
