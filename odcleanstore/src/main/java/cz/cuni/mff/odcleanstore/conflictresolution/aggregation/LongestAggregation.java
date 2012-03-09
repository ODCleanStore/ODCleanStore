package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

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
            EnumAggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator) {

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     * @param {@inheritDoc}
     * @return true iff the value is a string literal
     */
    @Override
    protected boolean isAggregable(Node value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
