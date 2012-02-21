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
final class TopCAggregation extends SelectedValueAggregation {
    /** 
     * Returns C quads with highest quality where C is cardinality of the quad's
     * predicate. 
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
     * @return always true
     */
    @Override
    protected boolean isAggregable(TripleItem value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
