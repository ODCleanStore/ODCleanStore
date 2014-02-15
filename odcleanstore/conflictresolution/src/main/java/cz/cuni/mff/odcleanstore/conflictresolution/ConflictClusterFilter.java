package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.List;

import org.openrdf.model.Statement;

/**
 * Additional filter on quads in a conflict cluster.
 * This filter is applied after canonical URIs are resolved, duplicities removed
 * A conflict cluster is a set of quads sharing the same subject and predicate.
 * @author Jan Michelfeit
 */
public interface ConflictClusterFilter {
    /**
     * Filters quads in a conflict cluster.
     * It is recommended to detect if any quads will be filtered and if not, return argument conflictCluster unchanged.
     * MUST return an instance of {@link java.util.RandomAccess} and result MUST be sorted 
     *      by objects and graph names. MAY modify argument conflictCluster and return it.
     * @param conflictCluster quads in the conflict cluster; the quads are sorted by object and graph name
     * @param context context object for the conflict resolution (containing resolution settings, additional metadata etc.)
     * @return filtered quads; MUST be an instance of {@link java.util.RandomAccess} and MUST be sorted 
     *      by objects and graph names
     */
    List<Statement> filter(List<Statement> conflictCluster, CRContext context);
}
