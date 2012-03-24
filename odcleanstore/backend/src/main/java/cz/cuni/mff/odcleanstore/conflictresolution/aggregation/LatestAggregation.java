package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;

/**
 * @todo
 * @author Jan Michelfeit
 */
class LatestAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public LatestAggregation(
            AggregationSpec aggregationSpec,
            UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Returns the quad with the latest stored time from conflictingQuads.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {

        throw new UnsupportedOperationException("Not supported yet.");
        // in case of equality return the triple with highest score
    }
}
