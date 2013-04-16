package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.openrdf.model.Resource;

/**
 * Map of metadata for named graphs from the RDF store.
 *
 * @author Jan Michelfeit
 */
public class NamedGraphMetadataMap {
    /** Map containing metadata for named graphs indexed by named graph URI. */
    private final Map<String, NamedGraphMetadata> metadataMap;

    /** Initializes an empty named graph metadata map. */
    public NamedGraphMetadataMap() {
        metadataMap = new TreeMap<String, NamedGraphMetadata>();
    }

    /**
     * Returns metadata for a given named graph.
     * @param namedGraph {@link Resource} with URI of the selected named graph; null represents the default named graph (context)
     * @return metadata for the selected named graph or null if metadata are unknown
     */
    public NamedGraphMetadata getMetadata(Resource namedGraph) {
        return metadataMap.get(namedGraph == null ? null : namedGraph.stringValue());
    }

    /**
     * Returns metadata for a given named graph URI.
     * @param namedGraphURI URI of a named graph
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
     *        must not be null
     */
    public void addMetadata(NamedGraphMetadata namedGraphMetadata) {
        this.metadataMap.put(namedGraphMetadata.getNamedGraphURI(), namedGraphMetadata);
    }

    /**
     * Clears contents of this map.
     */
    public void clear() {
        this.metadataMap.clear();
    }

    @Override
    public String toString() {
        return this.metadataMap.toString();
    }
}
