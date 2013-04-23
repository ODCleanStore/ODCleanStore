/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import org.openrdf.model.Model;

/**
 * @author Jan Michelfeit
 */
public interface CRContext {

    Model getMetadata();
    
    Model getContextStatements();
    
    ResolutionStrategy getResolutionStrategy();
    
    ResolvedStatementFactory getResolvedStatementFactory();
}
