package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;
import java.util.Iterator;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;

/**
 * Conflict resolution component.
 * The conflict resolution process resolves conflicts in place of objects of
 * triples in the input graph, taking into consideration owl:sameAs links and
 * aggregation settings (see ConflictResolverSpec).
 *
 * @author Jan Michelfeit
 */
public interface ConflictResolver {
    /**
     * Apply conflict resolution process to the input graph and return result
     * as a collection of CRQuads.
     * @param quads collection of quads where conflicts are to be resolved
     * @return collection of quads derived from the input quads with resolved
     *         conflicts, quality estimate and source named graph information.
     * @throws ConflictResolutionException thrown when an error during the conflict
     *         resolution process occurs
     * @see ResolvedStatement
     */
    Collection<ResolvedStatement> resolveConflicts(Iterator<Statement> statements) throws ConflictResolutionException;

    /**
     * @param statements
     * @return
     * @throws ConflictResolutionException
     */
    Collection<ResolvedStatement> resolveConflicts(Collection<Statement> statements) throws ConflictResolutionException;
}
