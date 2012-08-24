package cz.cuni.mff.odcleanstore.queryexecution.impl;

import cz.cuni.mff.odcleanstore.queryexecution.EnumQueryError;
import cz.cuni.mff.odcleanstore.queryexecution.QueryExecutionException;
import cz.cuni.mff.odcleanstore.shared.ErrorCodes;

import java.util.Map;

/**
 * Represents a mapping of namespace prefixes to their corresponding URIs.
 * Immutable, thread-safe.
 * @author Jan Michelfeit
 */
/*package*/public class PrefixMapping {
    private Map<String, String> prefixToURIMap;

    /**
     * Initializes the prefix mapping with the given map of prefixes to URIs.
     * @param prefixToURIMap mapping of prefixes to URIs
     */
    public PrefixMapping(Map<String, String> prefixToURIMap) {
        this.prefixToURIMap = prefixToURIMap;
    }

    /**
     * Return the URI mapped by the given prefix.
     * @param prefix the searched prefix
     * @return the mapped URI or null if there is no mapping for prefix
     */
    public String get(String prefix) {
        return prefixToURIMap.get(prefix);
    }

    /**
     * Expands a prefixed name to a whole URI.
     * Assumes that if the given string contains a ':', it<em>is</em> a prefixed name.
     * @param prefixedName prefixed name to expand
     * @return the expended URI or null if no mapping is found
     * @throws QueryExecutionException used prefix has no mapping
     */
    public String expandPrefix(String prefixedName) throws QueryExecutionException {
        int colon = prefixedName.indexOf(':');
        if (colon < 0) {
            return prefixedName;
        }
        String prefix = prefixedName.substring(0, colon);
        String expandedPrefix = get(prefix);
        if (expandedPrefix == null) {
            throw new QueryExecutionException(
                    EnumQueryError.UNKNOWN_PREFIX, ErrorCodes.QE_PREFIX_MAPPING_UNKNOWN_ERR, "Unkown prefix " + prefix + ":");
        }
        return expandedPrefix + prefixedName.substring(colon + 1);
    }
}
