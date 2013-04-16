package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import java.util.Collections;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuadImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Aggregation method that returns all input triples unchanged.
 * In effect the aggregation doesn't do anything except for adding a quality estimate.
 *
 * @author Jan Michelfeit
 */
/*package*/final class NoneAggregation extends SelectedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public NoneAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    /**
     * Returns conflictingQuads unchanged, only wrapped as CRQuads with added
     * quality estimate.
     *
     * {@inheritDoc}
     *
     * The time complexity is quadratic with number of conflicting quads.
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {

        Collection<CRQuad> result = createResultCollection();

        for (Statement quad : conflictingQuads) {
            Collection<String> sourceNamedGraphs = Collections.singletonList(getSourceGraphURI(quad));
            double quality = computeQualitySelected(
                    quad,
                    sourceNamedGraphs,
                    conflictingQuads,
                    metadata);
            Statement resultQuad = VALUE_FACTORY.createStatement(
                    quad.getSubject(),
                    quad.getPredicate(),
                    quad.getObject(),
                    VALUE_FACTORY.createURI(uriGenerator.nextURI()));
            result.add(new CRQuadImpl(resultQuad, quality, sourceNamedGraphs));
        }
        return result;
    }
}
