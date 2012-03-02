package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationErrorStrategy;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.graph.LiteralTripleItem;
import cz.cuni.mff.odcleanstore.graph.Quad;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

import java.util.Collection;

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
     * @param errorStrategy {@inheritDoc}
     * @param uriGenerator {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            AggregationErrorStrategy errorStrategy,
            UniqueURIGenerator uriGenerator) {

        StringBuilder resultValue = new StringBuilder();
        boolean first = true;
        for (Quad quad : conflictingQuads) {
            if (!first) {
                resultValue.append(SEPARATOR);
            }
            first = false;
            // TODO: change toString to lexical value or something
            resultValue.append(quad.getObject().toString());
        }

        Quad firstTriple = conflictingQuads.iterator().next();
        Quad resultQuad = new Quad(
                firstTriple.getSubject(),
                firstTriple.getPredicate(),
                new LiteralTripleItem(resultValue.toString()),
                uriGenerator.nextURI());
        double score = computeQuality(null, conflictingQuads, metadata);
        Collection<String> sourceNamedGraphs = allSourceNamedGraphs(conflictingQuads);
        Collection<CRQuad> result = createSingleResultCollection(
                new CRQuad(resultQuad, score, sourceNamedGraphs));
        return result;
    }

    /**
     * {@inheritDoc}
     * @param {@inheritDoc}
     * @return always true
     * @todo return false for resources??
     */
    @Override
    protected boolean isAggregable(TripleItem value) {
        return true;
    }
}
