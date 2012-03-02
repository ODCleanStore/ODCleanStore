package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

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
            AggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator) {

        Collection<CRQuad> result = createResultCollection();

        for (Quad quad : conflictingQuads) {
            double quality = computeQuality(quad, conflictingQuads, metadata);
            Collection<String> sourceNamedGraphs = Collections.singletonList(quad.getNamedGraph());

            Quad resultQuad = new Quad(quad.getTriple(), uriGenerator.nextURI());
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
    protected boolean isAggregable(TripleItem value) {
        return true;
    }
}
