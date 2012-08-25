package cz.cuni.mff.odcleanstore.transformer;

import java.util.Collection;

/**
 * Container for all neccessary information about a transofmed graph for a custom transformer.
 *
 * @author Jan Michelfeit
 */
public interface TransformedGraph {
    /**
     * Returns URI of the transformed ("payload") named graph.
     * @return named graph URI
     */
    String getGraphName();

    /**
     * Returns the unique identifier of the transform named graph in ODCS.
     * @return id string
     */
    String getGraphId();
    
    /**
     * Returns URI of the named graph containing OCDS metadata about the transformed graph.
     * @return ODCS metadata graph URI
     */
    String getMetadataGraphName();
    
    /**
     * Returns URI of the named graph containing provenance metadata about the transformed graph
     * (given to Input Webservice).
     * @return provenance metadata graph URI
     */
    String getProvenanceMetadataGraphName();

    /**
     * Returns a collection of graphs attached to the transformed graph registered by
     * {@link #addAttachedGraph(String)} method.
     * Contents of the attached graphs may be changed by the Transformer.
     * @return collection of named graphs' URIs
     */
    Collection<String> getAttachedGraphNames();

    /**
     * Register an additional graph attached to the main transformed graph.
     * IMPORTANT NOTE: This method must be called BEFORE inserting any triples into the new named graph
     * (in order to ensure consistency after failure of the transformation).
     * Transformer must not edit any data in the dirty database except for the given transformed graph
     * and metadata graph and newly created graphs registered by this method.
     * @param attachedGraphName URI of the attached named graph
     * @throws TransformedGraphException error while registering the attached graph
     */
    void addAttachedGraph(String attachedGraphName) throws TransformedGraphException;

    /**
     * Marks the whole transformed graph (and the respective attached graphs) for deletion.
     * @throws TransformedGraphException error while marking the graph for deletion
     */
    void deleteGraph() throws TransformedGraphException;

    /**
     * Returns true iff the transformed graph has been marked for deletion.
     * @see #deleteGraph()
     * @return true iff the transformed graph has been marked for deletion
     */
    boolean isDeleted();
}
