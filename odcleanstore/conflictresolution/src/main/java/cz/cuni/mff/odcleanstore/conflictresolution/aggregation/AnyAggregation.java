package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuadImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Aggregation method that returns a single triple selected from input triples.
 *
 * @author Jan Michelfeit
 */
/*package*/final class AnyAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public AnyAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    /**
     * Returns a single triple selected from input triples wrapped as CRQuad.
     * Returns the first triple from triples to be aggregated.
     * If conflictingQuads are empty, returns an empty collection.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {

        if (conflictingQuads.isEmpty()) {
            return createResultCollection();
        }

        Statement firstQuad = conflictingQuads.iterator().next();
        Collection<String> sourceNamedGraphs = sourceNamedGraphsForObject(
                firstQuad.getObject(),
                conflictingQuads);
        double quality = computeQualitySelected(
                firstQuad,
                sourceNamedGraphs,
                conflictingQuads,
                metadata);
        Statement resultQuad = VALUE_FACTORY.createStatement(
                firstQuad.getSubject(),
                firstQuad.getPredicate(),
                firstQuad.getObject(),
                VALUE_FACTORY.createURI(uriGenerator.nextURI())); 
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuadImpl(resultQuad, quality, sourceNamedGraphs));
        return result;
    }
}
