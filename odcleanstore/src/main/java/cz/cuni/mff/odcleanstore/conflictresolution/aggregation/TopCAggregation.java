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
final class TopCAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public TopCAggregation(
            AggregationSpec aggregationSpec,
            UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Returns C quads with highest quality where C is cardinality of the quad's
     * predicate.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata metadata for named graphs occurring in conflictingQuads
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {

        throw new UnsupportedOperationException("Not supported yet.");
    }
}
