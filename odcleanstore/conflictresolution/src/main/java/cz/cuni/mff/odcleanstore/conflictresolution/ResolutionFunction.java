/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;


/**
 * Conflict resolution function.
 * Takes a set of RDF quads (statements) and produces {@link ResolvedStatement resolved quads} with resolved
 * conflicts according to given resolution settings.
 * @author Jan Michelfeit
 */
public interface ResolutionFunction { 
    /**
     * Resolves conflicts in the given RDF quads.
     * @param statements RDF quads to be resolved
     * @param context context object for the conflict resolution (containing resolution settings, additional metadata etc.)
     * @return collection of resolved statements
     * @throws ConflictResolutionException error during conflict resolution
     */
    Collection<ResolvedStatement> resolve(Model statements, CRContext context) throws ConflictResolutionException;
}
