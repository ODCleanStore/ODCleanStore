package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import cz.cuni.mff.odcleanstore.shared.Utils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Aggregation method that returns the average of input conflicting triples.
 * This aggregation is applicable only to quads with a numeric literal as their object.
 *
 * "Agree bonus" in quality calculation is not applied for this aggregation.
 *
 * @author Jan Michelfeit
 */
final class AvgAggegation extends CalculatedValueAggregation {
    /**
     * Returns a single quad where the object is the average of objects in
     * conflictingQuads.
     * This aggregation method may be applied only to quads with numeric values as their objects.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @param uriGenerator {@inheritDoc}
     * @param aggregationSpec {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            UniqueURIGenerator uriGenerator,
            AggregationSpec aggregationSpec) {

        Collection<CRQuad> result = createResultCollection();
        Collection<Quad> nonAggregableQuads = null;

        // Compute average value
        double sum = 0;
        double validNumbersCount = 0;
        Collection<String> sourceNamedGraphs = new ArrayList<String>();

        for (Quad quad : conflictingQuads) {
            double numberValue = Utils.tryConvertToDouble(quad.getObject());
            if (!Double.isNaN(numberValue)) {
                sum += numberValue;
                validNumbersCount++;
                sourceNamedGraphs.add(quad.getGraphName().getURI());
            } else {
                if (nonAggregableQuads == null) {
                    nonAggregableQuads = new ArrayList<Quad>();
                }
                handleNonAggregableObject(quad, result, aggregationSpec, this.getClass());
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
            double quality = computeQuality(
                    resultQuad,
                    sourceNamedGraphs,
                    Collections.<String>emptySet(),
                    //sourceNamedGraphsForObject(resultQuad.getObject(), conflictingQuads),
                    aggregableQuads,
                    metadata,
                    aggregationSpec);
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        }

        return result;
    }
}
