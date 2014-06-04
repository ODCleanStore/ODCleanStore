/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatementFactory;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;

import java.util.Collection;

/**
 * Basic implementation of {@link CRContext}.
 * @author Jan Michelfeit
 */
public class CRContextImpl implements CRContext {
    private final Collection<Statement> conflictingStatements;
    private final Model metadata;
    private final ResolutionStrategy resolutionStrategy;
    private final Resource canonicalSubject;
    private final URI canonicalProperty;
    private final ResolvedStatementFactory resolvedStatementFactory;

    /**
     * @param conflictingStatements conflicting RDF quads
     * @param metadata metadata related to the quads being resolved
     * @param resolutionStrategy conflict resolution strategy
     * @param resolvedStatementFactory factory for resolved statements
     */
    public CRContextImpl(Collection<Statement> conflictingStatements, Model metadata, ResolutionStrategy resolutionStrategy,
            ResolvedStatementFactory resolvedStatementFactory, Resource canonicalSubject, URI canonicalProperty) {
        this.conflictingStatements = conflictingStatements;
        this.metadata = metadata;
        this.resolvedStatementFactory = resolvedStatementFactory;
        this.resolutionStrategy = resolutionStrategy;
        this.canonicalSubject = canonicalSubject;
        this.canonicalProperty = canonicalProperty;
    }

    @Override
    public ResolvedStatementFactory getResolvedStatementFactory() {
        return resolvedStatementFactory;
    }

    @Override
    public Model getMetadata() {
        return metadata;
    }
    
    @Override
    public Collection<Statement> getConflictingStatements() {
        return conflictingStatements;
    }
    
    @Override
    public ResolutionStrategy getResolutionStrategy() {
        return resolutionStrategy;
    }

    @Override
    public Resource getCanonicalSubject() {
        return canonicalSubject;
    }

    @Override
    public URI getCanonicalProperty() {
        return canonicalProperty;
    }
}
