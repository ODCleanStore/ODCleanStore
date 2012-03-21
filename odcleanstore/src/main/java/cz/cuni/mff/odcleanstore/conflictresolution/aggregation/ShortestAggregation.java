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
class ShortestAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public ShortestAggregation(
            AggregationSpec aggregationSpec,
            UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Returns a single quad where the object is the shortest value form objects
     * in conflictingQuads.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {

        throw new UnsupportedOperationException("Not supported yet.");
    }
}
