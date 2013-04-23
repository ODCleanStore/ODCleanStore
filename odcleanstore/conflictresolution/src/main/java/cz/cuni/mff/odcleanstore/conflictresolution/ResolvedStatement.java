package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

/**
 * An RDF quad enriched with (CR) quality estimate and a list of named graphs
 * the respective triple is derived from.
 *
 * @author Jan Michelfeit
 */
public interface ResolvedStatement {
    /**
     * Return the wrapped quad (statement).
     * @return the wrapped quad
     */
    Statement getStatement(); 

    /**
     * Return quality of the wrapped quad.
     * @return quality estimate of the quad
     */
    double getConfidence();

    /**
     * Collection of URIs of named graphs the triple is derived from.
     * @return a collection of named graph URIs
     */
    Collection<Resource> getSourceGraphNames();
}
