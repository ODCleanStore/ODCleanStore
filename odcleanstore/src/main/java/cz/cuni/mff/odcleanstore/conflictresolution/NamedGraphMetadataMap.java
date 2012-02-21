package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Map of metadata for named graphs from the RDF store.
 * 
 * @author Jan Michelfeit
 */
public class NamedGraphMetadataMap {
    /** Map containing metadata for named graphs indexed by named graph URI. */
    private Map<String, NamedGraphMetadata> metadataMap;
    
    /** Initializes an empty named graph metadata map. */
    public NamedGraphMetadataMap() {
        metadataMap = new TreeMap<String, NamedGraphMetadata>();
    }
    
    /**
     * Returns metadata for a named graph identified by its URI.
     * @param namedGraphURI URI of the selected named graph
     * @return metadata for the selected named graph or null if metadata are unknown
     */
    public NamedGraphMetadata getMetadata(String namedGraphURI) {
        return metadataMap.get(namedGraphURI);
    }
    
    /**
     * Returns set of named graph URIs for which there are metadata contained
     * in this instance.
     * @return set of named graph URIs
     */
    public Collection<NamedGraphMetadata> listMetadata() {
        return metadataMap.values();
    }
    
    /**
     * Adds metadata for a named graph. 
     * If a named graph with the same URI is already present, the metadata in
     * this map are replaced.
     * @param namedGraphMetadata metadata to be set for the named graph; 
     *      must not be null
     */
    public void addMetadata(/*URI namedGraphURI, */NamedGraphMetadata namedGraphMetadata) {
        this.metadataMap.put(namedGraphMetadata.getNamedGraphURI(), namedGraphMetadata);
    }
    
    /**
     * Clears contents of this map.
     */
    public void clear() {
        this.metadataMap.clear();
    }
    
    /**
     * Returns true iff this map contains metadata for the selected named graph.
     * @param namedGraphURI URI of the named graph
     * @return true iff the map contains metadata for the selected named graph.
     */
    public boolean containsNamedGraph(String namedGraphURI) {
        return this.metadataMap.containsKey(namedGraphURI);
    }
}
