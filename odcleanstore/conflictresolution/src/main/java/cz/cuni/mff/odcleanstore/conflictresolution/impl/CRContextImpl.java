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
 * @author Jan Michelfeit
 */
public class CRContextImpl implements CRContext {
    private final Model metadata;
    private final Model statementsModel;
    private ResolutionStrategy resolutionStrategy;
    private final ResolvedStatementFactory resolvedStatementFactory;
    private Resource subject;
    
    public CRContextImpl(Model statementsModel, Model metadata, ResolvedStatementFactory resolvedStatementFactory) {
        this.statementsModel = statementsModel;
        this.metadata = metadata;
        this.resolvedStatementFactory = resolvedStatementFactory;
    }
    
    public void setSubject(Resource subject) {
        this.subject = subject;
    }
    
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
