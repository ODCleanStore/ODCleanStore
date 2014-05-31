/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatementFactory;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import java.util.Collection;

/**
 * Basic implementation of {@link CRContext}.
 * @author Jan Michelfeit
 */
public class CRContextImpl implements CRContext {
    private final Collection<Statement> conflictingStatements;
    private final Model metadata;
    private final ResolutionStrategy resolutionStrategy;
    private final ResolvedStatementFactory resolvedStatementFactory;

    /**
     * @param conflictingStatements conflicting RDF quads
     * @param metadata metadata related to the quads being resolved
     * @param resolutionStrategy conflict resolution strategy
     * @param resolvedStatementFactory factory for resolved statements
     */
    public CRContextImpl(Collection<Statement> conflictingStatements, Model metadata, ResolutionStrategy resolutionStrategy, ResolvedStatementFactory resolvedStatementFactory) {
        this.conflictingStatements = conflictingStatements;
        this.metadata = metadata;
        this.resolvedStatementFactory = resolvedStatementFactory;
        this.resolutionStrategy = resolutionStrategy;
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
}
