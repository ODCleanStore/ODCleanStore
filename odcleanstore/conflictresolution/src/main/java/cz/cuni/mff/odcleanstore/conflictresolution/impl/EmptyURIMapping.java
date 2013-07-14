/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import org.openrdf.model.URI;

import cz.cuni.mff.odcleanstore.conflictresolution.URIMapping;

/**
 * An empty URI mapping. Maps every URI to itself.
 * @author Jan Michelfeit
 */
public class EmptyURIMapping implements URIMapping {
    private static final EmptyURIMapping INSTANCE = new EmptyURIMapping();
    
    /**
     * Return the shared default instance of this class.
     * @return shared instance of this class
     */
    public static URIMapping getInstance() {
        return INSTANCE;
    }
    
    @Override
    public URI mapURI(URI uri) {
        return uri;
    }

    @Override
    public String getCanonicalURI(String uri) {
        return uri;
    }
}