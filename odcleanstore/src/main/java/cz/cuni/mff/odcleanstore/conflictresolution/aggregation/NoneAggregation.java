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
final class NoneAggregation extends SelectedValueAggregation {
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
     * @param uriGenerator {@inheritDoc}
     * @param aggregationSpec {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            UniqueURIGenerator uriGenerator,
            AggregationSpec aggregationSpec) {

        Collection<CRQuad> result = createResultCollection();

        for (Quad quad : conflictingQuads) {
            Collection<String> sourceNamedGraphs =
                    Collections.singletonList(quad.getGraphName().getURI());
            double quality = computeQuality(quad, sourceNamedGraphs, conflictingQuads,
                    metadata, aggregationSpec);
            Quad resultQuad = new Quad(Node.createURI(uriGenerator.nextURI()), quad.getTriple());
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        }
        return result;
    }
}
