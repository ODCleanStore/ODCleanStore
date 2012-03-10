package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.EnumAggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import cz.cuni.mff.odcleanstore.shared.Utils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Aggregation method that returns an average of input conflicting triples.
 * This aggregation is applicable only to quads with a numeric literal as their object.
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

        Collection<CRQuad> result = createResultCollection();
        
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
                handleNonAggregableObject(quad, errorStrategy, result, this.getClass());
            }
        }
        
        if (validNumbersCount > 0) {
            double averageValue = sum / validNumbersCount;
            Quad firstQuad = conflictingQuads.iterator().next();
            Quad resultQuad = new Quad(
                    Node.createURI(uriGenerator.nextURI()),
                    firstQuad.getSubject(),
                    firstQuad.getPredicate(),
                    Node.createLiteral(LiteralLabelFactory.create(averageValue)));
            double quality = computeQuality(resultQuad, sourceNamedGraphs, metadata);
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        }

        return result;
    }
}
