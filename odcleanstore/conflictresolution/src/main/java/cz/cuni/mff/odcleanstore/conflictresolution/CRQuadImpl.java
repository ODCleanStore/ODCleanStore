package cz.cuni.mff.odcleanstore.conflictresolution;

import java.util.Collection;

import org.openrdf.model.Statement;

/**
 * Immutable implementation of {@link CRQuad}.
 *
 * @author Jan Michelfeit
 */
public class CRQuadImpl implements CRQuad {
    /** Wrapped RDF quad. */
    private final Statement quad;

    /** Quality estimate of the triple. */
    private final double quality;

    /** Collection of URIs of named graphs the triple is derived from. */
    private final Collection<String> sourceNamedGraphURIs;

    /**
     * @param quad an RDF quad to wrap; must not be null
     * @param quality quality estimate of the wrapped quad
     * @param sourceNamedGraphURIs collection of named graphs the quad is derived from;
     *        must not be null
     */
    public CRQuadImpl(Statement quad, double quality, Collection<String> sourceNamedGraphURIs) {
        assert quad != null;
        assert sourceNamedGraphURIs != null;
        this.quad = quad;
        this.quality = quality;
        this.sourceNamedGraphURIs = sourceNamedGraphURIs;
    }

    /**
     * Return the wrapped quad.
     * @return the wrapped quad
     */
    @Override
    public final Statement getQuad() {
        return quad;
    }

    /**
     * Return quality of the wrapped quad.
     * @return quality estimate of the quad
     */
    @Override
    public final double getQuality() {
        return quality;
    }

    /**
     * Collection of URIs of named graphs the triple is derived from.
     * @return a collection of named graph URIs
     */
    @Override
    public final Collection<String> getSourceNamedGraphURIs() {
        return sourceNamedGraphURIs;
    }

    /**
     * Return a human-readable string "&lt;quad&gt; quality".
     * @return human-readable representation of this CRQuad
     */
    @Override
    public String toString() {
        return quad.toString() + " " + Double.toString(quality);
    }
}
