package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.impl.NodeComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;
import java.util.Comparator;

/**
 * Base class for aggregation methods that include only triples selected from
 * conflicting input triples and don't return new triples calculated from
 * multiple conflicting triples.
 *
 * @author Jan Michelfeit
 */
/*package*/abstract class SelectedValueAggregation extends AggregationMethodBase {
    /**
     * A comparator used to sort quads by object and named graph.
     */
    protected static final ObjectNamedGraphComparator OBJECT_NG_COMPARATOR = new ObjectNamedGraphComparator();

    /**
     * Comparator of {@link Quad Quads} comparing first by objects, second by named graph.
     */
    protected static class ObjectNamedGraphComparator implements Comparator<Quad> {
        @Override
        public int compare(Quad q1, Quad q2) {
            int objectComparison = NodeComparator.compare(q1.getObject(), q2.getObject());
            if (objectComparison != 0) {
                return objectComparison;
            } else {
                return NodeComparator.compare(q1.getGraphName(), q2.getGraphName());
            }
        }
    }

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public SelectedValueAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    /**
     * Aggregates quads in conflictingQuads into one or more result quads and
     * calculates quality estimate and source information for each of the result
     * quads. The aggregated triples are selected from conflictingQuads and have
     * the original source.
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public abstract Collection<CRQuad> aggregate(Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata);

    /**
     * {@inheritDoc}
     *
     * The quality is the maximum score among source named graphs.
     */
    @Override
    protected double computeBasicQuality(
            Quad resultQuad,
            Collection<String> sourceNamedGraphs,
            NamedGraphMetadataMap metadata) {

        assert (sourceNamedGraphs.size() > 0) : "Illegal argument: sourceNamedGraphs must not be empty";

        double maximumQuality = 0;
        for (String sourceNamedGraphURI : sourceNamedGraphs) {
            NamedGraphMetadata namedGraphMetadata = metadata.getMetadata(sourceNamedGraphURI);
            double sourceQuality = getSourceQuality(namedGraphMetadata);
            if (sourceQuality > maximumQuality) {
                maximumQuality = sourceQuality;
            }
        }
        return maximumQuality;
    }

    /**
     * @see #computeQuality(Quad,Collection,Collection,Collection,NamedGraphMetadataMap)
     *
     *      In case of values selected from input quads, parameters sourceNamedGraphs and
     *      agreeNamedGraphs of computeQuality() are identical. This is a utility function that
     *      wraps this fact.
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
    protected double computeQualitySelected(
            Quad resultQuad,
            Collection<String> sourceNamedGraphs,
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata) {
        return computeQuality(
                resultQuad,
                sourceNamedGraphs,
                sourceNamedGraphs,
                conflictingQuads,
                metadata);
    }
}
