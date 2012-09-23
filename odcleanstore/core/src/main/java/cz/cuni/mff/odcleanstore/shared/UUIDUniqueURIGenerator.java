package cz.cuni.mff.odcleanstore.shared;

import java.util.UUID;

/**
 * Generates unique URIs in format <graphPrefix><UUID>-<orderNumber>.
 * This class is thread-safe and guarantees that generated URIs are unique even among multiple instances.
 * 
 * @author Jan Michelfeit
 */
public class UUIDUniqueURIGenerator implements UniqueURIGenerator {
    /** Prefix of generated named graph URIs. */
    private String namedGraphURIPrefix;
    
    /** Counter for generating unique named graph URIs. */
    private volatile long uriCount = 0;

    /**
     * Crate a new instance generating URIs with the specified prefix.
     * @param uriPrefix prefix of generated named graph URIs
     */
    public UUIDUniqueURIGenerator(String uriPrefix) {
        this.namedGraphURIPrefix = uriPrefix + UUID.randomUUID().toString() + "-";
    }

    @Override
    public String nextURI() {
        long newCount = ++uriCount;
        return namedGraphURIPrefix + newCount;
    }
}
