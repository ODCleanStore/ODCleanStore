package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Base class for aggregation methods where the result is calculated from 
 * all the conflicting input quads.
 * Particularly, the source of each aggregated quad is the union of all sources
 * of the input triples.
 * 
 * @author Jan Michelfeit
 */
abstract class CalculatedValueAggregation extends AggregationMethodBase {
    /**
     * Calculates result quads from all conflictingQuads. The source of the
     * aggregated triples is thus the union of all sources of the input triples.
     * 
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @param errorStrategy {@inheritDoc}
     * @param uriGenerator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public abstract Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata, 
            AggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator);
    
    /**
     * {@inheritDoc}
     * 
     * Since the aggregated result is based on all conflicting triples,
     * the quality is calcualted as an average of their respective source qualities.
     * 
     * @param resultQuad {@inheritDoc}; can be null
     * @param conflictingQuads  {@inheritDoc}; must not be null
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     * @see #getSourceQuality(NamedGraphMetadata)
     */
    @Override
    protected double computeQuality(
            Quad resultQuad, 
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata) {
        
        int totalConflictingQuads = 0;
        double sourceQualityAvg = 0;
        
        for (Quad quad : conflictingQuads) {
            NamedGraphMetadata namedGraphMetadata = metadata.getMetadata(quad.getNamedGraph());
            sourceQualityAvg += getSourceQuality(namedGraphMetadata);
            totalConflictingQuads++;
        }
        
        assert (totalConflictingQuads > 0)
                : "Illegal argument: conflictingQuads must not be empty";
        
        sourceQualityAvg /= totalConflictingQuads;
        return sourceQualityAvg;
    }
    
    /**
     * Returns a set (without duplicates) of all named graphs of quads in 
     * conflictingQuads.
     * @param conflictingQuads A collection of quads
     * @return a set of named graphs of all quads in conflictingQuads
     */
    protected Collection<String> allSourceNamedGraphs(Collection<Quad> conflictingQuads) {
        if (conflictingQuads.size() == 1) {
            // A singleton collection will do for a single quad
            return Collections.singleton(conflictingQuads.iterator().next().getNamedGraph());
        }
        
        Set<String> result = new HashSet<String>(conflictingQuads.size());
        for (Quad quad : conflictingQuads) {
            result.add(quad.getNamedGraph());
        }
        return result;
    }
}
