package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
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
     * @param errorStrategy {@inheritDoc}
     * @param uriGenerator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            EnumAggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator) {

        Collection<CRQuad> result = createResultCollection();

        for (Quad quad : conflictingQuads) {
            double quality = computeQuality(quad, conflictingQuads, metadata);
            Collection<String> sourceNamedGraphs =
                    Collections.singletonList(quad.getGraphName().getURI());
            Quad resultQuad = new Quad(Node.createURI(uriGenerator.nextURI()), quad.getTriple());
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * @param {@inheritDoc}
     * @return always true
     */
    @Override
    protected boolean isAggregable(Node value) {
        return true;
    }
}
