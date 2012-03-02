package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import java.util.Collection;
import java.util.Date;

/**
 * Aggregation method that returns the quad with the highest (conflict resolution)
 * quality or the newest stored time in case of equality of quality.
 * 
 * @author Jan Michelfeit
 */
final class BestAggregation extends SelectedValueAggregation {
    /**
     * Returns a single triple selected from input triples wrapped as CRQuad.
     * Returns a triple with the highest (conflict resolution quality) or the
     * newest stored time in case of equality of quality.
     * If conflictingQuads are empty, returns an empty collection.
     * 
     * {@inheritDoc}
     * 
     * The time complexity is quadratic with number of conflicting quads.
     * (for each quad calculates its quality in linear time).
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

        if (conflictingQuads.isEmpty()) {
            return createResultCollection();
        }

        Quad bestQuad = null;
        double bestQuadQuality = Double.NEGATIVE_INFINITY;
        for (Quad quad : conflictingQuads) {
            double quality = computeQuality(quad, conflictingQuads, metadata);
            if (quality > bestQuadQuality) {
                // Prefer higher quality
                bestQuad = quad;
                bestQuadQuality = quality;
            } else if (quality == bestQuadQuality) {
                // In case of equality prefer newer date
                assert bestQuad != null; // quality shouldn't be NEGATIVE_INFINITY
                Date storedDate = metadata.getMetadata(quad.getNamedGraph()).getStored();
                Date bestQuadStored = metadata.getMetadata(bestQuad.getNamedGraph()).getStored();
                if (storedDate != null
                        && (bestQuadStored == null || storedDate.after(bestQuadStored))) {
                    bestQuad = quad;
                    bestQuadQuality = quality;
                }
            }
        }

        // bestQuad is not null because conflictingQuads is not empty
        assert bestQuad != null;
        // By not cloning quad for resultQuad we rely on quad being immutable
        Quad resultQuad = new Quad(bestQuad.getTriple(), uriGenerator.nextURI());
        Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(
                bestQuad.getObject(),
                conflictingQuads);
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, bestQuadQuality, sourceNamedGraphs));
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
