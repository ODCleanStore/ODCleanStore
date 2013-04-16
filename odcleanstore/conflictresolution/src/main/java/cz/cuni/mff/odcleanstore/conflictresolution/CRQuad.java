package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Statement;

/**
 * An RDF quad enriched with (CR) quality estimate and a list of named graphs
 * the respective triple is derived from.
 *
 * @author Jan Michelfeit
 */
public interface CRQuad {
    /**
     * Return the wrapped quad (statement).
     * @return the wrapped quad
     */
    Statement getQuad(); 

    /**
     * Return quality of the wrapped quad.
     * @return quality estimate of the quad
     */
    double getQuality();

    /**
     * Collection of URIs of named graphs the triple is derived from.
     * @return a collection of named graph URIs
     */
    Collection<String> getSourceNamedGraphURIs();
}
