package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Aggregation method for single quads.
 *
 * Behavior of an aggregation method aggregating a single quad is well-defined:
 * <ul>
 * <li>There is a single source named graph - the quad's named graph,</li>
 * <li>The result quality is the score of the named graph.</li>
 * </ul>
 * Thus result of all aggregations on a single quad is the same.
 * For better effectivity, one can use this class instead of any other
 * aggregation method.
 *
 * Usable <i>only</i> for aggregation of a single quad.
 *
 * @author Jan Michelfeit
 */
/*package*/final class SingleValueAggregation extends AggregationMethodBase {
    private static final Logger LOG = LoggerFactory.getLogger(SingleValueAggregation.class);

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public SingleValueAggregation(
            AggregationSpec aggregationSpec,
            UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Returns the single value from conflictingQuads wrapped as a CRQuad.
     * Argument conflictingQuads must contain exactly one quad.
     *
     * @param conflictingQuads {@inheritDoc}; must contain exactly one quad.
     * @param metadata metadata for named graphs occurring in conflictingQuads
     * @return {@inheritDoc}
     * @throw IllegalArgumentException thrown when conflictingQuads doesn't
     *        contain exactly one quad
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {

        if (conflictingQuads.size() != 1) {
            LOG.error("{} quads given to SingleValueAggregation.", conflictingQuads.size());
            throw new IllegalArgumentException(
                    "SingleValueAggregation accepts only a single conflicting quad, "
                            + conflictingQuads.size()
                            + " given.");
        }

        Quad firstQuad = conflictingQuads.iterator().next();
        Collection<String> sourceNamedGraphs =
                Collections.singleton(firstQuad.getGraphName().getURI());
        double quality = computeBasicQuality(firstQuad, sourceNamedGraphs, metadata);
        Quad resultQuad = new Quad(Node.createURI(uriGenerator.nextURI()), firstQuad.getTriple());
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, quality, sourceNamedGraphs));
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * In case of a single quad the whole quality is the score of the source named graph.
     */
    @Override
    protected double computeBasicQuality(
            Quad resultQuad,
            Collection<String> sourceNamedGraphs,
            NamedGraphMetadataMap metadata) {

        NamedGraphMetadata resultMetadata = metadata.getMetadata(resultQuad.getGraphName());
        double resultQuality = getSourceQuality(resultMetadata);
        return resultQuality;
    }
}
