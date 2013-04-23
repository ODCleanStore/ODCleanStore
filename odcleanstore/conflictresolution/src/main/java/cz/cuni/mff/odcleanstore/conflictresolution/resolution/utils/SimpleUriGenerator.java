package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Generator of unique named graph URIs.
 * This class is not thread-safe.
 *
 * @author Jan Michelfeit
 */
public class SimpleUriGenerator implements UniqueURIGenerator {
    private static final ValueFactory VALUE_FACTORY = ValueFactoryImpl.getInstance();
    
    /** Prefix of generated named graph URIs. */
    private final String namedGraphURIPrefix;

    /** Counter for generating unique named graph URIs. */
    private long lastNamedGraphId = 0;

    /**
     * Crate a new instance generating URIs with the specified prefix.
     * @param uriPrefix prefix of generated named graph URIs
     */
    public SimpleUriGenerator(String uriPrefix) {
        this.namedGraphURIPrefix = uriPrefix;
    }

    @Override
    public URI nextURI() {
        ++lastNamedGraphId;
        return VALUE_FACTORY.createURI(namedGraphURIPrefix + lastNamedGraphId);
    }
}
