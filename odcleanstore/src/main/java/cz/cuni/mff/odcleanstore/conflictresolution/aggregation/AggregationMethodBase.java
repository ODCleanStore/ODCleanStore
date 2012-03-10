package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

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
            EnumAggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator);

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
     * Applies the given aggregation error strategy to a quad that cannot be aggregated by the
     * given aggregation method.
     * In case of the RETURN_ALL error strategy, the given quad is added to the result.
     * 
     * @param nonAggregableQuad the quad that couldn't be aggregated
     * @param errorStrategy aggregation error strategy to follow
     * @param result result of aggregation; a new CRQuad may be added to this collection
     * @param aggregationMethod aggregation class where the aggregation error occured
     */
    protected void handleNonAggregableObject(
            Quad nonAggregableQuad, 
            EnumAggregationErrorStrategy errorStrategy, 
            Collection<CRQuad> result,
            Class<? extends AggregationMethodBase> aggregationMethod) {
        
        LOG.debug("Value {} cannot be aggregated with {}.", 
                nonAggregableQuad.getObject(), aggregationMethod.getSimpleName());
        
        switch (errorStrategy) {
        case RETURN_ALL:
            result.add(new CRQuad(
                    nonAggregableQuad,
                    0, // TODO: compute real quality!
                    Collections.singleton(nonAggregableQuad.getGraphName().getURI())));
            break;
        case IGNORE:
            // do nothing
            break;
        default:
            LOG.error("Unhandled aggregation error strategy {}.", errorStrategy);
            throw new RuntimeException("Unhandled error strategy!");
        }
    }

    /**
     * Factory method for collections returned by
     * {@link #aggregate(Collection, NamedGraphMetadataMap, EnumAggregationErrorStrategy)
     * aggregate()}.
     * @return an empty collection
     */
    protected Collection<CRQuad> createResultCollection() {
        return new LinkedList<CRQuad>();
    }

    /**
     * Factory method for collections returned by
     * {@link #aggregate(Collection, NamedGraphMetadataMap, EnumAggregationErrorStrategy)
     * aggregate()} with a single value.
     * @param resultQuad a quad initially added to the result collection
     * @return an collection containing only resultQuad
     */
    protected Collection<CRQuad> createSingleResultCollection(CRQuad resultQuad) {
        return Collections.singletonList(resultQuad);
    }
}
