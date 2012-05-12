package cz.cuni.mff.odcleanstore.conflictresolution;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

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
     * @see CRQuad
     */
    Collection<CRQuad> resolveConflicts(Collection<Quad> quads) throws ConflictResolutionException;
}
