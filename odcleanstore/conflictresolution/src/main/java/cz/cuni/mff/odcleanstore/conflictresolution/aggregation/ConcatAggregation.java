package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.Collection;
import java.util.Collections;

import com.hp.hpl.jena.graph.Node;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Aggregation method that returns the concatenation of all conflicting values.
 * @author Jan Michelfeit
 */

/*package*/final class ConcatAggregation extends CalculatedValueAggregation {
    /** Separator of concatenated values. */
    public static final String SEPARATOR = "; ";

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public ConcatAggregation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
    }

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
     * @return {@inheritDoc} 
     */
    @Override
    public Collection<CRQuad> aggregate(Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {
        StringBuilder resultValue = new StringBuilder();
        boolean first = true;
        for (Quad quad : conflictingQuads) {
            if (!first) {
                resultValue.append(SEPARATOR);
            }
            first = false;
            Node object = quad.getObject();
            String stringRepresenation = object.isLiteral() ? object.getLiteralLexicalForm() : object.toString();
            resultValue.append(stringRepresenation);
        }

        Quad firstQuad = conflictingQuads.iterator().next();
        Quad resultQuad = new Quad(
                Node.createURI(uriGenerator.nextURI()),
                firstQuad.getSubject(),
                firstQuad.getPredicate(),
                Node.createLiteral(resultValue.toString()));
        Collection<String> sourceNamedGraphs = allSourceNamedGraphs(conflictingQuads);

        double quality = computeQualityNoAgree(
                resultQuad,
                sourceNamedGraphs,
                Collections.singleton(resultQuad), // difference penalty doesn't make sense here
                metadata);

        Collection<CRQuad> result = createSingleResultCollection(new CRQuad(resultQuad, quality, sourceNamedGraphs));
        return result;
    }
}
