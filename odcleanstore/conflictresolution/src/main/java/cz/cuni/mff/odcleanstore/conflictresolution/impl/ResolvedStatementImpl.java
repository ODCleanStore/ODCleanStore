package cz.cuni.mff.odcleanstore.conflictresolution.impl;

import java.util.Collection;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;

/**
 * Immutable implementation of {@link ResolvedStatement}.
 *
 * @author Jan Michelfeit
 */
public class ResolvedStatementImpl implements ResolvedStatement {
    /** Wrapped RDF quad. */
    private final Statement statement;

    /** Quality estimate of the triple. */
    private final double quality;

    /** Collection of URIs of named graphs the triple is derived from. */
    private final Collection<Resource> sourceGraphNames;

    /**
     * @param statement an RDF quad to wrap; must not be null
     * @param quality quality estimate of the wrapped quad
     * @param sourceGraphNames collection of named graphs the quad is derived from;
     *        must not be null
     */
    public ResolvedStatementImpl(Statement statement, double quality, Collection<Resource> sourceGraphNames) {
        assert statement != null;
        assert sourceGraphNames != null;
        this.statement = statement;
        this.quality = quality;
        this.sourceGraphNames = sourceGraphNames;
    }

    /**
     * Return the wrapped quad.
     * @return the wrapped quad
     */
    @Override
    public final Statement getStatement() {
        return statement;
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
    public final Collection<Resource> getSourceGraphNames() {
        return sourceGraphNames;
    }

    /**
     * Return a human-readable string "&lt;quad&gt; quality".
     * @return human-readable representation of this CRQuad
     */
    @Override
    public String toString() {
        return "(" + statement.toString()
                + "; " + Double.toString(quality)
                + "; " + sourceGraphNames
                + ")";
    }
}
