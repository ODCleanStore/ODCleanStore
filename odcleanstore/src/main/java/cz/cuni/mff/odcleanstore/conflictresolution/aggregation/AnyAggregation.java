package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * Aggregation method that returns a single triple selected from input triples.
 *
 * @author Jan Michelfeit
 */
final class AnyAggregation extends SelectedValueAggregation {

    /**
     * Returns a single triple selected from input triples wrapped as CRQuad.
     * Returns the first triple from triples to be aggregated.
     * If conflictingQuads are empty, returns an empty collection.
     *
     * {@inheritDoc}
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

        if (conflictingQuads.isEmpty()) {
            return createResultCollection();
        }

        Quad firstQuad = conflictingQuads.iterator().next();
        Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(
                firstQuad.getObject(),
                conflictingQuads);
        double quality = computeQualitySelected(firstQuad, sourceNamedGraphs, conflictingQuads,
                metadata, aggregationSpec);
        Quad resultQuad = new Quad(Node.createURI(uriGenerator.nextURI()), firstQuad.getTriple());
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, quality, sourceNamedGraphs));
        return result;
    }
}
