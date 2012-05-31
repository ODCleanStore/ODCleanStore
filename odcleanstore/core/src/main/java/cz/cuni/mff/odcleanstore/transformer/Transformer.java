package cz.cuni.mff.odcleanstore.transformer;

/**
 * Interface of a custom transformer.
 *
 * The transformer can modify the given input named graph, attach a new named graph to the
 * transformed graph (see {@link TransformedGraph#addAttachedGraph(String)}) or modify any of the
 * already attached named graphs.
 * The caller must ensure that the transformed named graphs (and the respective attached graphs) are
 * not modified while the Transformer is working on it.
 * A new instance of the transformer is created for each named graph to process.
 *
 * @author Jan Michelfeit
 */
public interface Transformer {
    /**
     * Transforms a new graph in the dirty database.
     * @param inputGraph holder for the transformed graph.
     * @param context context of the transformation
     * @throws TransformerException exception
     */
    void transformNewGraph(TransformedGraph inputGraph, TransformationContext context) throws TransformerException;

    /**
     * Transforms an existing graph in the clean database.
     * @param inputGraph holder for the transformed graph.
     * @param context context of the transformation
     * @throws TransformerException exception
     */
    void transformExistingGraph(TransformedGraph inputGraph, TransformationContext context)
            throws TransformerException;

    /**
     * Called when the instance of the transformer is no longer needed.
     * This method is suitable for releasing resources etc.
     * @throws TransformerException exception
     */
    void shutdown() throws TransformerException;
}
