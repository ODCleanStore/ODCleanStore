package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Base class for all aggregation methods implemented in ODCleanStore.
 * 
 * @author Jan Michelfeit
 */
abstract class AggregationMethodBase implements AggregationMethod {
    private static final Logger LOG = LoggerFactory.getLogger(AggregationMethodBase.class);

    /**
     * Default score of a named graph or a publisher used if the respective
     * score is unknown.
     * @todo determine best value
     */
    public static final double SCORE_IF_UNKNOWN = 1;

    /**
     * Weight of the source named graph score in source quality calculation.
     * The sum of weights may be different from 1.
     * @see #getSourceQuality(NamedGraphMetadata)
     * @see #PUBLISHER_SCORE_WEIGHT
     * @todo determine best value
     */
    protected static final double NAMED_GRAPH_SCORE_WEIGHT = 0.8;

    /**
     * Weight of the publisher source in source quality calculation.
     * The sum of weights may be different from 1.
     * @see #getSourceQuality(NamedGraphMetadata)
     * @see #NAMED_GRAPH_SCORE_WEIGHT
     */
    protected static final double PUBLISHER_SCORE_WEIGHT = 0.2;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Collection<CRQuad> aggregate(
            Collection<Quad> conflictingTriples,
            NamedGraphMetadataMap metadata,
            AggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator);

    /**
     * Indicates whether a TripleItem instance is valid for this
     * aggregation method.
     * @param value a TripleItem for aggregation
     * @return true iff the aggregation can be applied to value
     * @see AggregationErrorStrategy
     */
    protected abstract boolean isAggregable(TripleItem value);

    /**
     * Compute quality estimate of a selected quad taking into consideration
     * possible conflicting quads and source named graph metadata.
     * @param resultQuad the quad for which quality is to be computed
     * @param conflictingQuads other quads conflicting with resultQuad
     *        (for what is meant by conflicting quads see
     *        {@link AggregationMethod#aggregate(Collection, NamedGraphMetadataMap, AggregationErrorStrategy)
     *        aggregate()})
     * @param metadata metadata of source named graphs for resultQuad
     *        and conflictingQuads
     * @return quality estimate of resultQuad as a number from [0,1]
     */
    protected abstract double computeQuality(
            Quad resultQuad,
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata);

    /**
     * Calculated quality of a source of a named graph.
     * The quality is calculated as a weighted average of (error localization)
     * score of the named graph and score of its publisher with weights
     * defined in {@link #NAMED_GRAPH_SCORE_WEIGHT} and {@link #PUBLISHER_SCORE_WEIGHT}.
     * @param metadata metadata of the estimated named graph (or null if unknown)
     * @return quality of source of the named graph as a value from [0,1]
     * @see #SCORE_IF_UNKNOWN
     */
    protected double getSourceQuality(NamedGraphMetadata metadata) {
        // Weighted average of metadata.getScore() and metadata.getPublisherScore()
        if (metadata == null) {
            LOG.debug("No metadata given for source quality computation, using default scores.");
            return SCORE_IF_UNKNOWN;
        }

        Double namedGraphScore = metadata.getScore();
        if (namedGraphScore == null) {
            LOG.debug("No score for named graph {}, using default score.",
                    metadata.getNamedGraphURI());
            namedGraphScore = SCORE_IF_UNKNOWN;
        }

        Double publisherScore = metadata.getPublisherScore();
        if (publisherScore == null) {
            LOG.debug("No score for the publisher of named graph {}, using default score.",
                    metadata.getNamedGraphURI());
            publisherScore = SCORE_IF_UNKNOWN;
        }

        double quality = namedGraphScore * NAMED_GRAPH_SCORE_WEIGHT
                + publisherScore * PUBLISHER_SCORE_WEIGHT;
        quality /= NAMED_GRAPH_SCORE_WEIGHT + PUBLISHER_SCORE_WEIGHT;
        return quality;
    }

    /**
     * Factory method for collections returned by
     * {@link #aggregate(Collection, NamedGraphMetadataMap, AggregationErrorStrategy) aggregate()}.
     * @return an empty collection
     */
    protected Collection<CRQuad> createResultCollection() {
        return new LinkedList<CRQuad>();
    }

    /**
     * Factory method for collections returned by
     * {@link #aggregate(Collection, NamedGraphMetadataMap, AggregationErrorStrategy) aggregate()}
     * with a single value.
     * @param resultQuad a quad initially added to the result collection
     * @return an collection containing only resultQuad
     */
    protected Collection<CRQuad> createSingleResultCollection(CRQuad resultQuad) {
        return Collections.singletonList(resultQuad);
    }
}
