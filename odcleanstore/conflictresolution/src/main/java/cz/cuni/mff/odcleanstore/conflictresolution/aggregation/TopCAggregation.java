package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * @todo
 * @author Jan Michelfeit
 */
/*package*/final class TopCAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public TopCAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
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
            Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {

        throw new UnsupportedOperationException("Not supported yet.");
    }
}
