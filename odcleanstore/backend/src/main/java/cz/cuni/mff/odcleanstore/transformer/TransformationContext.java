package cz.cuni.mff.odcleanstore.transformer;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

import java.io.File;
import java.io.InputStream;

/**
 * Context for a custom transformer.
 * Provides access to resources a transformer may need.
 *
 * @author Jan Michelfeit
 */
public interface TransformationContext {
    /**
     * Reference to the dirty database.
     * The dirty database contains new incoming named graphs.
     * @return reference to the dirty database SPARQL endpoint
     */
    SparqlEndpoint getDirtyDatabaseEndpoint();

    /**
     * Reference to the clean database.
     * The clean database contains already processed named graphs.
     * @return reference to the clean database SPARQL endpoint
     */
    SparqlEndpoint getCleanDatabaseEndpoint();

    /**
     * Configuration file for the Transformer (obtained from ODCS database).
     * TODO: return String instead of InputStream?
     * @return configuration as an InputStream
     */
    InputStream getTransformerConfiguration();

    /**
     * Returns reference to a directory reserved for the Transformer's needs.
     * Contents of the directory may be freely changed, the directory itself mustn't be
     * deleted, however.
     * @return reference to a directory
     */
    File getTransformerDirectory();

    /**
     * Returns the type of transformation.
     * This method should return {@link EnumTransformationType.NEW} when transformation is invoked
     * by calling {@link Transformer.transformNewGraph()} and
     * {@link EnumTransformationType.EXISTING} when invoked by {@link
     * Transformer.transformExistingGraph()}.
     * @return type of transformation
     */
    EnumTransformationType getTransformationType();
}
