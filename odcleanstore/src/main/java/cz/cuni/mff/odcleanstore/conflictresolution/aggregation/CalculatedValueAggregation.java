package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
     * @param uriGenerator {@inheritDoc}
     * @param aggregationSpec {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public abstract Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            UniqueURIGenerator uriGenerator,
            AggregationSpec aggregationSpec);

    /**
     * {@inheritDoc}
     *
     * Since the aggregated result is based on all conflicting triples,
     * the quality is the average score of source named graphs.
     */
    @Override
    protected double computeBasicQuality(
            Quad resultQuad,
            Collection<String> sourceNamedGraphs,
            NamedGraphMetadataMap metadata) {

        int namedGraphCount = 0;
        double scoreSum = 0;

        for (String sourceNamedGraphURI : sourceNamedGraphs) {
            NamedGraphMetadata namedGraphMetadata = metadata.getMetadata(sourceNamedGraphURI);
            scoreSum += getSourceQuality(namedGraphMetadata);
            namedGraphCount++;
        }

        assert (namedGraphCount > 0) : "Illegal argument: sourceNamedGraphs must not be empty";
        double scoreAverage = scoreSum / namedGraphCount;
        return scoreAverage;
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
            return Collections.singleton(
                    conflictingQuads.iterator().next().getGraphName().getURI());
        }

        Set<String> result = new HashSet<String>(conflictingQuads.size());
        for (Quad quad : conflictingQuads) {
            result.add(quad.getGraphName().getURI());
        }
        return result;
    }
}
