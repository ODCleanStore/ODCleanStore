package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Aggregation method that returns the quad with the highest (conflict resolution)
 * quality or the newest stored time in case of equality of quality.
 *
 * @author Jan Michelfeit
 */
final class BestAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public BestAggregation(
            AggregationSpec aggregationSpec,
            UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

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
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {

        if (conflictingQuads.isEmpty()) {
            return createResultCollection();
        }

        Quad bestQuad = null;
        double bestQuadQuality = Double.NEGATIVE_INFINITY;
        for (Quad quad : conflictingQuads) {
            // TODO: optimize: do not use sourceNamedGraphsForObject, but sort by object
            // (as in AllAggregation)
            Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(
                    quad.getObject(),
                    conflictingQuads);
            double quality = computeQualitySelected(
                    quad,
                    sourceNamedGraphs,
                    conflictingQuads,
                    metadata);
            if (quality > bestQuadQuality) {
                // Prefer higher quality
                bestQuad = quad;
                bestQuadQuality = quality;
            } else if (quality == bestQuadQuality) {
                // In case of equality prefer newer date
                assert bestQuad != null; // quality shouldn't be NEGATIVE_INFINITY
                NamedGraphMetadata quadMetadata = metadata.getMetadata(quad.getGraphName());
                NamedGraphMetadata bestQuadMetadata = metadata.getMetadata(bestQuad.getGraphName());
                if (quadMetadata != null
                        && quadMetadata.getStored() != null
                        && (bestQuadMetadata == null
                            || bestQuadMetadata.getStored() == null
                            || quadMetadata.getStored().after(bestQuadMetadata.getStored()))) {
                    bestQuad = quad;
                    bestQuadQuality = quality;
                }
            }
        }

        // bestQuad is not null because conflictingQuads is not empty
        assert bestQuad != null;
        // By not cloning quad for resultQuad we rely on quad being immutable
        Quad resultQuad = new Quad(Node.createURI(uriGenerator.nextURI()), bestQuad.getTriple());
        Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(
                bestQuad.getObject(),
                conflictingQuads);
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, bestQuadQuality, sourceNamedGraphs));
        return result;
    }
}
