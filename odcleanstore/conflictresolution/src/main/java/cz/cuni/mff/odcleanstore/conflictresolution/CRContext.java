/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import org.openrdf.model.Model;

/**
 * Object representing context for application of a conflict resolution function.
 * @author Jan Michelfeit
 */
public interface CRContext {

    /**
     * Returns metadata related to the resolved data.
     * @return metadata as an RDF model
     */
    Model getMetadata();
    
    /**
     * RDF quads related to the quads being resolved.
     * At least all quads with the same subject are guaranteed to be contained in the model 
     * @return context quads as an RDF model
     */
    Model getContextStatements();
    
    /**
     * Get conflict resolution settings for the current set of quads being resolved.
     * @return effective conflict resolution settings
     */
    ResolutionStrategy getResolutionStrategy();
    
    /**
     * Returns a factory object for resulting resolved statements.
     * @return factory for resolved statements.
     */
    ResolvedStatementFactory getResolvedStatementFactory();
}
