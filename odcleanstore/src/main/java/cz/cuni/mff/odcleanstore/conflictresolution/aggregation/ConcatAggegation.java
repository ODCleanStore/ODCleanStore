package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Collection;
import java.util.Collections;

/**
 * @todo
 * @author Jan Michelfeit
 */

final class ConcatAggegation extends CalculatedValueAggregation {
    /** Separator of concatenated values. */
    public static final String SEPARATOR = ", ";

    /**
     * Returns a single quad where the object is concatenation of string
     * representations of object in conflictingQuads.
     * Concatenated values are separated with "{@value #SEPARATOR}".
     * If conflictingQuads is empty, the object is an empty string.
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

        StringBuilder resultValue = new StringBuilder();
        boolean first = true;
        for (Quad quad : conflictingQuads) {
            if (!first) {
                resultValue.append(SEPARATOR);
            }
            first = false;
            resultValue.append(quad.getObject().toString());
        }

        Quad firstQuad = conflictingQuads.iterator().next();
        Quad resultQuad = new Quad(
                Node.createURI(uriGenerator.nextURI()),
                firstQuad.getSubject(),
                firstQuad.getPredicate(),
                Node.createLiteral(resultValue.toString()));
        Collection<String> sourceNamedGraphs = allSourceNamedGraphs(conflictingQuads);

        // Passing singleton collection as conflicting values disables
        // decreasing of score with difference of conflicting values, which doesn't make
        // sense for the CONCAT aggregation
        double quality = computeQuality(resultQuad, sourceNamedGraphs,
                Collections.singleton(resultQuad), metadata, aggregationSpec);

        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, quality, sourceNamedGraphs));
        return result;
    }
}
