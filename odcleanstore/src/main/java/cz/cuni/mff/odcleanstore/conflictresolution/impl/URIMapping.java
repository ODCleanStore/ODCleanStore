package cz.cuni.mff.odcleanstore.conflictresolution.impl;

/**
 * Mapping of an URI to another URI.
 * @author Jan Michelfeit
 */
interface URIMapping {

    /**
     * Returns a mapping to a cannonical URI for the selected URI.
     * If uri has no defined mapping or is mapped to itself, returns null.
     * @param uri an RDF reource URI to map
     * @return the canonical URI (an URI the uri argument maps to) or null if
     *      the canonical URI is identical to uri
     */
    String mapURI(String uri);
}
