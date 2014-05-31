/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import org.openrdf.model.Model;
import org.openrdf.model.Statement;

import java.util.Collection;

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
     * Conflicting RDF quads to be considered during quality calculation.
     * @return conflicting RDF quads as an RDF model
     */
    Collection<Statement> getConflictingStatements();

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
