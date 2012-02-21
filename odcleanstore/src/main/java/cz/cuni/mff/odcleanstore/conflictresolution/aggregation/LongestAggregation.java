package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * @todo
 * @author Jan Michelfeit
 */
class LongestAggregation extends SelectedValueAggregation {
    /** 
     * Returns a single quad where the object is the longest value from objects 
     * in conflictingQuads.
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
     * {@inheritDoc}
     * @param {@inheritDoc}
     * @return true iff the value is a string literal
     */
    @Override
    protected boolean isAggregable(TripleItem value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
