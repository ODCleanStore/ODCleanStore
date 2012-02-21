package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import java.util.Collections;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Aggregation method for single quads.
 * 
 * Behavior of an aggregation method aggregating a single quad is well-defined:
 * <ul>
 *  <li>There is a single source named graph - the quad's named graph,</li>
 *  <li>The result quality is the score of the named graph.</li>
 * </ul>
 * Thus result of all aggregations on a single quad is the same. 
 * For better effectivity, one can use this class instead of any other 
 * aggregation method.
 * 
 * Usable <i>only</i> for aggregation of a single quad.
 * 
 * @author Jan Michelfeit
 */
final class SingleValueAggregation extends AggregationMethodBase {
    private static final Logger LOG = LoggerFactory.getLogger(SingleValueAggregation.class);
    
    /**
     * Returns the single value from conflictingQuads wrapped as a CRQuad.
     * Argument conflictingQuads must contain exactly one quad.
     * 
     * @param conflictingQuads {@inheritDoc}; must contain exactly one quad.
     * @param metadata {@inheritDoc}
     * @param errorStrategy {@inheritDoc}
     * @param uriGenerator {@inheritDoc}
     * @return {@inheritDoc}
     * @throw IllegalArgumentException thrown when conflictingQuads doesn't 
     *      contain exactly one quad
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata, 
            AggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator) {
        
        if (conflictingQuads.size() != 1) {
            LOG.error("{} quads given to SingleValueAggregation.", conflictingQuads.size());
            throw new IllegalArgumentException(
                    "SingleValueAggregation accepts only a single conflicting quad, "
                    + conflictingQuads.size()
                    + " given.");
        }
        
        Quad firstQuad = conflictingQuads.iterator().next();
        double score = computeQuality(firstQuad, conflictingQuads, metadata);
        Collection<String> sourceNamedGraphs = Collections.singleton(firstQuad.getNamedGraph());
        Quad resultQuad = new Quad(firstQuad.getTriple(), uriGenerator.nextURI());
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, score, sourceNamedGraphs));
        return result;
    }
    
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
        
        NamedGraphMetadata resultMetadata = metadata.getMetadata(resultQuad.getNamedGraph());
        double resultQuality = getSourceQuality(resultMetadata);
        return resultQuality;
    }

    /**
     * {@inheritDoc}
     * @param {@inheritDoc}
     * @return always true
     */
    @Override
    protected boolean isAggregable(TripleItem value) {
        return true;
    }
}
