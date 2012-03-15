package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
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
     * Aggregates quads in conflictingQuads into one or more result quads and
     * calculates quality estimate and source information for each of the result
     * quads. The aggregated triples are selected from conflictingQuads and have
     * the original source.
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
     * The quality is the maximum score among source named graphs.
     */
    @Override
    protected double computeBasicQuality(
            Quad resultQuad,
            Collection<String> sourceNamedGraphs,
            NamedGraphMetadataMap metadata) {

        assert (sourceNamedGraphs.size() > 0)
                : "Illegal argument: sourceNamedGraphs must not be empty";

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
            // if not necessary (only zero or one named graph in the result)
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
