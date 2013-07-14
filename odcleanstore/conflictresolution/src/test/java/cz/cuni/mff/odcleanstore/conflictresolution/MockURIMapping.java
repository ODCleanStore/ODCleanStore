/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Map;

import org.openrdf.model.URI;
import org.openrdf.model.impl.ValueFactoryImpl;

/**
 * Mock URI mapping based on a {@link Map} of mappings.
 * @author Jan Michelfeit
 */
public class MockURIMapping implements URIMapping {
    private Map<URI, URI> mapping;
    
    /**
     * @param mapping map used for the mock mapping
     */
    public MockURIMapping(Map<URI, URI> mapping) {
        this.mapping = mapping;
    }
    
    @Override
    public URI mapURI(URI uri) {
        if (mapping.containsKey(uri)) {
            return mapping.get(uri);
        } else {
            return uri;
        }
    }

    @Override
    public String getCanonicalURI(String uri) {
        return mapURI(ValueFactoryImpl.getInstance().createURI(uri)).stringValue();
    }

}
