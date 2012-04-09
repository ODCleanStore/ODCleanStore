package cz.cuni.mff.odcleanstore.transformer;

import java.util.Collection;

/**
 * Container for all neccessary information about a transofmed graph for a custom transformer.
 *
 * @author Jan Michelfeit
 */
public interface TransformedGraph {
    /**
     * TODO
     * Returns an editable model of the transformed graph.
     * (jenom pokud to pujde udelat)
     */
    //Model getModel();

    /**
     * Returns URI of the transformed named graph.
     * @return named graph URI
     */
    String getGraphName();

    /**
     * Returns unique identifier in ODCS of the transform named graph.
     * @return id string
     */
    String getGraphId();

    /**
     * Returns URI of the named graph containing (provenance) metadata about the transformed graph.
     * @return named graph URI
     */
    String getMetadataGraphName();

    /**
     * Returns a model of the named graph containing (provenance) metadata about the transformed
     * graph.
     */
    //Model getMetadataModel();

    /**
     * Returns a collection of graphs attached to the transformed graph registered by
     * {@link #addAttachedGraph(String)} method.
     * Contents of the attached graphs may be changed by the Transformer.
     * @return collection of named graphs' URIs
     */
    Collection<String> getAttachedGraphNames();

    /**
     * Register an additional graph attached to the main transformed graph.
     * @param attachedGraphName URI of the attached named graph
     */
    void addAttachedGraph(String attachedGraphName);

    /**
     * Marks the whole transformed graph (and the respective attached graphs) for deletion.
     */
    void deleteGraph();

    /**
     * Returns true iff the transformed graph has been marked for deletion.
     * @see #deleteGraph()
     * @return true iff the transformed graph has been marked for deletion
     */
    boolean isDeleted();
}
