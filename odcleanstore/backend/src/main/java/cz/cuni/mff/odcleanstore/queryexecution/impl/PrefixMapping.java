package cz.cuni.mff.odcleanstore.queryexecution.impl;

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
     */
    public String expandPrefix(String prefixedName) {
        int colon = prefixedName.indexOf(':');
        if (colon < 0) {
            return prefixedName;
        } else {
            String uri = get(prefixedName.substring(0, colon));
            return uri == null ? null : uri + prefixedName.substring(colon + 1);
        }
    }
}
