package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import com.hp.hpl.jena.graph.Node;

/**
 * Mapping of an URI to another URI.
 * @author Jan Michelfeit
 */
/*package*/interface URIMapping {
    /**
     * Returns a mapping to a canonical URI for the selected URI.
     * If URI has no defined mapping or is mapped to itself, returns null.
     * @param uri a Node_URI instance to map
     * @return the canonical URI (an URI the uri argument maps to) or null if the canonical URI is
     *         identical to URI
     */
    Node mapURI(Node uri);

    /**
     * Returns the canonical URI for the given URI.
     * @param uri the URI to map
     * @return the mapped URI
     */
    String getCanonicalURI(String uri);
}
