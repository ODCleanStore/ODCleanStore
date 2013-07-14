/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolutionStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatementFactory;

/**
 * Basic implementation of {@link CRContext}.
 * @author Jan Michelfeit
 */
public class CRContextImpl implements CRContext {
    private final Model metadata;
    private final Model statementsModel;
    private ResolutionStrategy resolutionStrategy;
    private final ResolvedStatementFactory resolvedStatementFactory;
    private Resource subject;
    
    /**
     * @param statementsModel RDF quads related to the quads being resolved.
     * @param metadata metadata related to the quads being resolved
     * @param resolvedStatementFactory factory for resolved statements
     */
    public CRContextImpl(Model statementsModel, Model metadata, ResolvedStatementFactory resolvedStatementFactory) {
        this.statementsModel = statementsModel;
        this.metadata = metadata;
        this.resolvedStatementFactory = resolvedStatementFactory;
    }
    
    /**
     * Sets subject for the conflict cluster being resolved.
     * Setting the subject limits result of {@link #getContextStatements()} to return
     * only quads with this subject.
     * @param subject conflict cluster subject
     */
    public void setSubject(Resource subject) {
        this.subject = subject;
    }
    /**
     * Sets conflict resolution settings for the current set of quads being resolved. 
     * @param resolutionStrategy conflict resolution strategys
     */
    public void setResolutionStrategy(ResolutionStrategy resolutionStrategy) {
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
    public Model getContextStatements() { 
        return statementsModel.filter(subject, null, null);
    }
    
    @Override
    public ResolutionStrategy getResolutionStrategy() {
        return resolutionStrategy;
    }
}
