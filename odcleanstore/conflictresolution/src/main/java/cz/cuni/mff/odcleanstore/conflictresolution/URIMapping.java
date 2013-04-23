package cz.cuni.mff.odcleanstore.conflictresolution;

import org.openrdf.model.URI;


/**
 * Mapping of an URI to its equivalent URI (canonical URI).
 * @author Jan Michelfeit
 */
public interface URIMapping {
    /**
     * Returns a mapping to a canonical URI for the selected URI.
     * If URI has no defined mapping or is mapped to itself, returns <code>uri</code>.
     * @param uri a Node_URI instance to map
     * @return the canonical URI (an URI the uri argument maps to)
     */
    URI mapURI(URI uri);

    /**
     * Returns the canonical URI for the given URI.
     * @param uri the URI to map
     * @return the mapped URI
     */
    String getCanonicalURI(String uri);
} 