package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

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
        
        Quad firstQuad = conflictingQuads.iterator().next();
        double score = computeQuality(firstQuad, conflictingQuads, metadata);
        Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(
                firstQuad.getObject(), 
                conflictingQuads);
        Quad resultQuad = new Quad(firstQuad.getTriple(), uriGenerator.nextURI());
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, score, sourceNamedGraphs));
        return result;
    }

    /**
     * {@inheritDoc}
     * @param value {@inheritDoc}
     * @return always true
     */
    @Override
    protected boolean isAggregable(TripleItem value) {
        return true;
    }
}
