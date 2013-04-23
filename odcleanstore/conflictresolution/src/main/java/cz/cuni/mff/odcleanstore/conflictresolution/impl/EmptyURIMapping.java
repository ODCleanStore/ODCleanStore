/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import org.openrdf.model.URI;

import cz.cuni.mff.odcleanstore.conflictresolution.URIMapping;

/**
 * @author Jan Michelfeit
 */
public class EmptyURIMapping implements URIMapping {
    private static final EmptyURIMapping instance = new EmptyURIMapping();
    
    public static URIMapping getInstance() {
        return instance;
    }
    
    @Override
    public URI mapURI(URI uri) {
        return null;
    }

    @Override
    public String getCanonicalURI(String uri) {
        return uri;
    }
}