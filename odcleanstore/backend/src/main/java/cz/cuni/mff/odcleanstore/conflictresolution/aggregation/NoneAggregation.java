package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;
import java.util.Collections;

/**
 * Aggregation method that returns all input triples unchanged.
 * In effect the aggregation doesn't do anything except for adding a quality estimate.
 *
 * @author Jan Michelfeit
 */
/*package*/final class NoneAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public NoneAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Returns conflictingQuads unchanged, only wrapped as CRQuads with added
     * quality estimate.
     *
     * {@inheritDoc}
     *
     * The time complexity is quadratic with number of conflicting quads.
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {

        Collection<CRQuad> result = createResultCollection();

        for (Quad quad : conflictingQuads) {
            Collection<String> sourceNamedGraphs =
                    Collections.singletonList(quad.getGraphName().getURI());
            double quality = computeQualitySelected(
                    quad,
                    sourceNamedGraphs,
                    conflictingQuads,
                    metadata);
            Quad resultQuad = new Quad(Node.createURI(uriGenerator.nextURI()), quad.getTriple());
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        }
        return result;
    }
}
