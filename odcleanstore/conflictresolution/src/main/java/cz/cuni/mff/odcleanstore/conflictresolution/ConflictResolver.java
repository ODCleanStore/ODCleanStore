package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;
import java.util.Iterator;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.exceptions.ConflictResolutionException;

/**
 * RDF conflict resolver.
 * The conflict resolution process resolves conflicts in place of objects of
 * triples in the input graph, taking into consideration owl:sameAs links and
 * conflict resolution settings.
 * @author Jan Michelfeit
 */
public interface ConflictResolver {
    /**
     * Apply conflict resolution process to the given RDF quads and return result
     * as a collection of {@link ResolvedStatement}.
     * @param statements iterator over RDF quads where conflicts are to be resolved
     * @return collection of quads derived from the input quads with resolved
     *         conflicts, (F-)quality estimate and provenance information.
     * @throws ConflictResolutionException error during the conflict resolution process 
     * @see ResolvedStatement
     */
    Collection<ResolvedStatement> resolveConflicts(Iterator<Statement> statements) throws ConflictResolutionException;

    /**
     * Apply conflict resolution process to the given RDF quads and return result
     * as a collection of {@link ResolvedStatement}.
     * @param statements collection of RDF quads where conflicts are to be resolved
     * @return collection of quads derived from the input quads with resolved
     *         conflicts, (F-)quality estimate and provenance information.
     * @throws ConflictResolutionException error during the conflict resolution process 
     * @see ResolvedStatement
     */
    Collection<ResolvedStatement> resolveConflicts(Collection<Statement> statements)
            throws ConflictResolutionException;
}
