package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Aggregation method that returns the average of input conflicting triples.
 * This aggregation is applicable only to quads with a numeric literal as their object.
 *
 * "Agree bonus" in quality calculation is not applied for this aggregation.
 *
 * @author Jan Michelfeit
 */
/*package*/final class AvgAggregation extends CalculatedValueAggregation {
    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public AvgAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

    /**
     * Returns a single quad where the object is the average of objects in
     * conflictingQuads.
     * This aggregation method may be applied only to quads with numeric values as their objects.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {
        Collection<CRQuad> result = createResultCollection();
        Collection<Quad> nonAggregableQuads = null;

        // Compute average value
        double sum = 0;
        double validNumbersCount = 0;
        Collection<String> sourceNamedGraphs = new ArrayList<String>();

        for (Quad quad : conflictingQuads) {
            double numberValue = AggregationUtils.convertToDoubleSilent(quad.getObject());
            if (!Double.isNaN(numberValue)) {
                sum += numberValue;
                validNumbersCount++;
                sourceNamedGraphs.add(quad.getGraphName().getURI());
            } else {
                if (nonAggregableQuads == null) {
                    nonAggregableQuads = new ArrayList<Quad>();
                }
                handleNonAggregableObject(quad, conflictingQuads, metadata, result, this.getClass());
                nonAggregableQuads.add(quad);
            }
        }

        if (validNumbersCount > 0) {
            // Get list of quads that were really aggregated;
            // optimized for the case of few non-aggregable quads
            Collection<Quad> aggregableQuads;
            if (nonAggregableQuads == null) {
                aggregableQuads = conflictingQuads;
            } else {
                aggregableQuads = new ArrayList<Quad>(conflictingQuads);
                aggregableQuads.removeAll(nonAggregableQuads);
            }

            double averageValue = sum / validNumbersCount;
            Quad firstQuad = conflictingQuads.iterator().next();
            Quad resultQuad = new Quad(
                    Node.createURI(uriGenerator.nextURI()),
                    firstQuad.getSubject(),
                    firstQuad.getPredicate(),
                    Node.createLiteral(LiteralLabelFactory.create(averageValue)));
            double quality = computeQualityNoAgree(
                    resultQuad,
                    // sourceNamedGraphsForObject(resultQuad.getObject(), conflictingQuads)
                    sourceNamedGraphs,
                    aggregableQuads,
                    metadata);
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        }

        return result;
    }
}