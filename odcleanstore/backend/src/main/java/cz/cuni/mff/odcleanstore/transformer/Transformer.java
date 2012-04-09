package cz.cuni.mff.odcleanstore.transformer;

/**
 * Interface of a custom transformer.
 *
 * The transformer can modify the given input named graph, attach a new named graph to the
 * transformed graph (see {@link TransformedGraph#addAttachedGraph(String)}) or modify any of the
 * already attached named graphs.
 * The caller must ensure that the transformed named graphs (and the respective attached graphs) are
 * not modified while the Transformer is working on it.
 *
 * @author Jan Michelfeit
 */
public interface Transformer {
    /**
     * Transforms a new graph in the dirty database.
     * @param inputGraph holder for the transformed graph.
     * @param context context of the transformation
     */
    void transformNewGraph(TransformedGraph inputGraph, TransformationContext context);

    /**
     * Transforms an existing graph in the clean database.
     * @param inputGraph holder for the transformed graph.
     * @param context context of the transformation
     */
    void transformExistingGraph(TransformedGraph inputGraph, TransformationContext context);
}
