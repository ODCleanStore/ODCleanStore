package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils;

import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Generator of unique named graph URIs.
 *
 * @author Jan Michelfeit
 */
public class SimpleUriGenerator implements UniqueURIGenerator {
    /** Prefix of generated named graph URIs. */
    private String namedGraphURIPrefix;

    /** Counter for generating unique named graph URIs. */
    private int lastNamedGraphId = 0;

    /**
     * Crate a new instance generating URIs with the specified prefix.
     * @param uriPrefix prefix of generated named graph URIs
     */
    public SimpleUriGenerator(String uriPrefix) {
        this.namedGraphURIPrefix = uriPrefix;
    }

    @Override
    public String nextURI() {
        ++lastNamedGraphId;
        return namedGraphURIPrefix + lastNamedGraphId;
    }
}
