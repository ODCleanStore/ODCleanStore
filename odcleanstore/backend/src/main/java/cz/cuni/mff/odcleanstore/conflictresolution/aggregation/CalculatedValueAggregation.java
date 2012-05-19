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
/*package*/abstract class CalculatedValueAggregation extends AggregationMethodBase {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public CalculatedValueAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Calculates result quads from all conflictingQuads. The source of the
     * aggregated triples is thus the union of all sources of the input triples.
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public abstract Collection<CRQuad> aggregate(Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata);

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
     * @see #computeQuality(Quad,Collection,Collection,Collection,NamedGraphMetadataMap)
     *
     *      For some calculated values, multiple sources agreeing on the same value is rather by
     *      coincidence and the quality shouldn't be increased. This methods wraps computeQuality()
     *      disabling the agree bonus.
     *
     * @param resultQuad the quad for which quality is to be computed
     * @param conflictingQuads other quads conflicting with resultQuad
     *        (for what is meant by conflicting quads see AggregationMethod#aggregate())
     * @param sourceNamedGraphs URIs of source named graphs containing triples used to calculate
     *        the result value; must not be empty
     * @param metadata metadata of source named graphs for resultQuad
     *        and conflictingQuads
     * @return quality estimate of resultQuad as a number from [0,1]
     */
    protected double computeQualityNoAgree(
            Quad resultQuad,
            Collection<String> sourceNamedGraphs,
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata) {
        return computeQuality(
                resultQuad,
                sourceNamedGraphs,
                Collections.<String>emptySet(),
                conflictingQuads,
                metadata);
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
            return Collections.singleton(conflictingQuads.iterator().next().getGraphName().getURI());
        }

        Set<String> result = new HashSet<String>(conflictingQuads.size());
        for (Quad quad : conflictingQuads) {
            result.add(quad.getGraphName().getURI());
        }
        return result;
    }
}
