package cz.cuni.mff.odcleanstore.conflictresolution;

import cz.cuni.mff.odcleanstore.graph.Quad;

import java.util.Collection;

/**
 * An RDF quad enriched with (CR) quality estimate and a list of named graphs
 * the respective triple is derived from.
 * Immutable.
 * 
 * @author Jan Michelfeit
 */
public class CRQuad {
    /** Wrapped RDF quad. */
    private Quad quad;
    
    /** Quality estimate of the triple. */
    private double quality;
    
    /** Collection of URIs of named graphs the triple is derived from. */
    private Collection<String> sourceNamedGraphURIs;

    /**
     * @param quad an RDF quad to wrap
     * @param quality quality estimate of the wrapped quad
     * @param sourceNamedGraphURIs cllection of named graphs the quad is derived from
     */
    public CRQuad(Quad quad, double quality, Collection<String> sourceNamedGraphURIs) {
        this.quad = quad;
        this.quality = quality;
        this.sourceNamedGraphURIs = sourceNamedGraphURIs;
    }

    /** 
     * Return the wrapped quad. 
     * @return the wrapped quad
     */
    public final Quad getQuad() {
        return quad;
    }
    
    /** 
     * Return quality of the wrapped quad. 
     * @return quality estimate of the quad
     */
    public final double getQuality() {
        return quality;
    }
    
    /** 
     * Collection of URIs of named graphs the triple is derived from.
     * @return a collection of named graph URIs
     */
    public final Collection<String> getSourceNamedGraphURIs() {
        return sourceNamedGraphURIs;
    }
}
