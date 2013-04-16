package cz.cuni.mff.odcleanstore.transformer;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

import java.io.File;

/**
 * Context for a custom transformer.
 * Provides access to resources a transformer may need.
 *
 * @author Jan Michelfeit
 */
public interface TransformationContext {
    /**
     * Reference to the dirty database, i.e. where the transformed graph is located.
     * The dirty database contains new incoming named graphs.
     * @return reference to the dirty database connection credentials
     */
    JDBCConnectionCredentials getDirtyDatabaseCredentials();

    /**
     * Reference to the clean database.
     * The clean database contains already processed named graphs.
     * @return reference to the clean database connection credentials
     */
    JDBCConnectionCredentials getCleanDatabaseCredentials();

    /**
     * Configuration file for the Transformer (obtained from ODCS database).
     * @return configuration as an InputStream
     */
    String getTransformerConfiguration();

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
