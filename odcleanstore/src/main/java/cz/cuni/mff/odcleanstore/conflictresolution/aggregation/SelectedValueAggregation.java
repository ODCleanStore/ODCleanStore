package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class for aggregation methods that include only triples selected from
 * conflicting input triples and don't return new triples calculated from
 * multiple conflicting triples.
 *
 * @author Jan Michelfeit
 */
abstract class SelectedValueAggregation extends AggregationMethodBase {
    /**
     * Node distance metric used in quality computation.
     * @see #computeQuality(Quad, Collection, NamedGraphMetadataMap)
     */
    private static/*final*/DistanceMetric distanceMetricInstance = new DistanceMetricImpl();

    /**
     * Aggregates quads in conflictingQuads into one or more result quads and
     * calculates quality estimate and source information for each of the result
     * quads. The aggregated triples are selected from conflictingQuads and have
     * the original source.
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
            EnumAggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator);

    /**
     * Return the default DistanceMetric instance.
     * @return Node distance metric that can be used in quality computation.
     */
    protected DistanceMetric getDistanceMetric() {
        return distanceMetricInstance;
    }

    /**
     * Compute quality estimate of a selected quad taking into consideration
     * possible conflicting quads and source named graph metadata.
     *
     * The computed quality depends on quality of the source of result and
     * on how different conflicting quads are.
     *
     * The exact formula is
     *
     * <pre>
     * q(V,v0) = sourceQuality(v0)
     *   * (1 - (SUM OF (sourceQuality(V[i]) * difference(V[i],v0))) / (SUM OF sourceQuality(V[i])))
     * </pre>
     *
     * (see documentation for explanation).
     * The time complexity O(n*d) where n is size of conflictingQuads and d is
     * the complexity of difference calculation.
     *
     * Precondition: resultQuad is expected to be in conflictingQuads.
     * @param resultQuad the quad for which quality is to be computed
     * @param conflictingQuads other quads conflicting with resultQuad
     *        (for what is meant by conflicting quads see AggregationMethod#aggregate())
     * @param metadata metadata of source named graphs for resultQuad
     *        and conflictingQuads
     * @return quality estimate of resultQuad as a number from [0,1]
     * @see #getSourceQuality(NamedGraphMetadata)
     */
    protected double computeQuality(
            Quad resultQuad,
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata) {

        NamedGraphMetadata resultMetadata = metadata.getMetadata(resultQuad.getGraphName());
        double resultQuality = getSourceQuality(resultMetadata);
        if (resultQuality == 0) {
            return resultQuality;
        }

        // Calculated distance average weighted by respective source qualities
        DistanceMetric distanceMetric = getDistanceMetric();
        double distanceAverage = 0;
        double totalSourceQuality = 0;
        for (Quad quad : conflictingQuads) {
            NamedGraphMetadata quadMetadata = metadata.getMetadata(quad.getGraphName());
            double quadQuality = getSourceQuality(quadMetadata);

            double resultDistance = distanceMetric.distance(
                    quad.getObject(), resultQuad.getObject());
            distanceAverage += quadQuality * resultDistance;
            totalSourceQuality += quadQuality;
        }

        // resultQuality cannot be zero (tested before) -> if sum of
        // conflictingQuads source qualitites is zero, resultQuality is not
        // among them -> precondition broken
        assert (totalSourceQuality > 0)
            : "Precondition broken: resultQuad is not present in conflictingQuads";

        distanceAverage /= totalSourceQuality;

        double result = resultQuality * (1 - distanceAverage);
        return result;
    }

    /**
     * Return a set (without duplicates) of named graphs of all quads that have the selected object
     * as their object.
     *
     * @param object the searched triple object
     * @param conflictingQuads searched quads
     * @return set of named graphs
     */
    protected Collection<String> sourceNamedGraphsForObject(
            Node object, Collection<Quad> conflictingQuads) {

        Set<String> namedGraphs = null;
        String firstNamedGraph = null;

        for (Quad quad : conflictingQuads) {
            if (!object.equals(quad.getObject())) {
                continue;
            }

            String newNamedGraph = quad.getGraphName().getURI();
            // Purpose of these if-else branches is to avoid creating HashSet
            // if not neccessary (only zero or one named graph in the result)
            if (firstNamedGraph == null) {
                firstNamedGraph = newNamedGraph;
            } else if (namedGraphs == null) {
                namedGraphs = new HashSet<String>();
                namedGraphs.add(firstNamedGraph);
                namedGraphs.add(newNamedGraph);
            } else {
                namedGraphs.add(newNamedGraph);
            }
        }

        if (firstNamedGraph == null) {
            return Collections.emptySet();
        } else if (namedGraphs == null) {
            return Collections.singleton(firstNamedGraph);
        } else {
            return namedGraphs;
        }
    }
}
