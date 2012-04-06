package cz.cuni.mff.odcleanstore.test;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

import java.io.InputStream;

/**
 * Context for a custom transformer.
 * Provides access to resources a transformer may need.
 *
 * @todo just a prototype
 * @author Jan Michelfeit
 */
public abstract class TransformationContext {
    public abstract SparqlEndpoint getCleanDatabaseEndpoint();

    public abstract SparqlEndpoint getDirtyDatabaseEndpoint();

    public abstract InputStream getTransformerConfiguration();
}
