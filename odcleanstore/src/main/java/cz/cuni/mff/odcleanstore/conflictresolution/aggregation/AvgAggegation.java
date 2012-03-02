package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import java.util.Collection;

/**
 * Aggregation method that returns an average of input conflicting triples.
 * This aggregation is applicable only to quads with a numeric literal as their object.
 * 
 * @author Jan Michelfeit
 */
final class AvgAggegation extends CalculatedValueAggregation {

    /**
     * Returns a single quad where the object is the average of objects in
     * conflictingQuads.
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

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @todo
     *       {@inheritDoc}
     * @param {@inheritDoc}
     * @return true iff the value is a numeric literal
     */
    @Override
    protected boolean isAggregable(TripleItem value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
