package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 * Wrapper for a resolved RDF triple returned from application of a conflict resolution function.
 * Contains the produced RDF triple together with its quality (F-quality) and source named graph
 * the respective triple was selected or derived from.
 *
 * @author Jan Michelfeit
 */
public interface ResolvedStatement {
    /**
     * Returns the wrapped RDF triple (statement).
     * @return RDF quad
     */
    Statement getStatement(); 

    /**
     * Return quality ("F-quality") of the wrapped triple.
     * @return quality as a number from interval [0,1]
     */
    double getQuality();

    /**
     * Set of named graph URIs of graphs the wrapped triple was selected or derived from.
     * @return a collection of named graph URIs
     */
    Collection<Resource> getSourceGraphNames();
}
