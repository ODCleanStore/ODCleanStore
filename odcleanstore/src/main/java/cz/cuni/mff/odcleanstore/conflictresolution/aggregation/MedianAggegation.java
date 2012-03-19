package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.EnumLiteralType;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;
import cz.cuni.mff.odcleanstore.shared.Utils;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;

import de.fuberlin.wiwiss.ng4j.Quad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Aggregation method that returns the median of input conflicting triples.
 * This aggregation is applicable only to quads with a literal as their object.
 *
 * "Agree bonus" in quality calculation is not applied for this aggregation.
 *
 * @author Jan Michelfeit
 */
class MedianAggegation extends CalculatedValueAggregation {
    private static final Logger LOG = LoggerFactory.getLogger(MedianAggegation.class);

    /**
     * Returns a single quad where the object is the median of objects in conflictingQuads.
     * The type of comparison (e.g. as numbers, strings, dates) is chosen based on the type of
     * the object of the first quad in conflictingQuads. Only literal objects can be aggregated.
     *
     * {@inheritDoc}
     *
     * @param conflictingQuads {@inheritDoc}
     * @param metadata {@inheritDoc}
     * @param uriGenerator {@inheritDoc}
     * @param aggregationSpec {@inheritDoc}
     * @return {@inheritDoc}
     *
     * @TODO: IMPORTANT: in conflictingQuads passed to computeQuality filter out quads
     *      that were handled by handleNonAggregableObject
     */
    @Override
    public Collection<CRQuad> aggregate(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            UniqueURIGenerator uriGenerator,
            AggregationSpec aggregationSpec) {

        Collection<CRQuad> result = createResultCollection();
        if (conflictingQuads.isEmpty()) {
            return result;
        }

        Quad firstQuad = conflictingQuads.iterator().next();
        if (!firstQuad.getObject().isLiteral()) {
            for (Quad quad : conflictingQuads) {
                handleNonAggregableObject(quad, result, aggregationSpec, this.getClass());
            }
            return result;
        }

        EnumLiteralType comparisonType = Utils.getLiteralType(firstQuad.getObject());
        switch (comparisonType) {
        case NUMERIC:
            return aggregateNumeric(conflictingQuads, metadata, uriGenerator, aggregationSpec);
        case DATE:
            // TODO
        case BOOLEAN:
            // TODO
        case STRING:
        case OTHER:
            return aggregateString(conflictingQuads, metadata, uriGenerator, aggregationSpec);
        default:
            LOG.error("Unhandled type of literal {} in {}.",
                    comparisonType.name(), this.getClass().getSimpleName());
            throw new RuntimeException("Unhandled type of literal");
        }
    }

    /**
     * @see #aggregate()
     * @param conflictingQuads see {@link #aggregate()}
     * @param metadata see {@link #aggregate()}
     * @param aggregationSpec see {@link #aggregate()}
     * @param uriGenerator see {@link #aggregate()}
     * @return see {@link #aggregate()}
     */
    private Collection<CRQuad> aggregateNumeric(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            UniqueURIGenerator uriGenerator,
            AggregationSpec aggregationSpec) {

        Collection<CRQuad> result = createResultCollection();
        ArrayList<Double> objects = new ArrayList<Double>();
        Collection<String> sourceNamedGraphs = new ArrayList<String>();

        for (Quad quad : conflictingQuads) {
            double numberValue = Utils.tryConvertToDouble(quad.getObject());
            if (!Double.isNaN(numberValue)) {
                objects.add(numberValue);
                sourceNamedGraphs.add(quad.getGraphName().getURI());
            } else {
                handleNonAggregableObject(quad, result, aggregationSpec, this.getClass());
            }
        }

        if (sourceNamedGraphs.isEmpty()) {
            return result;
        } else {
            Collections.sort(objects);
            int medianPosition = objects.size() / 2;
            Double medianValue = objects.get(medianPosition);
            Quad firstQuad = conflictingQuads.iterator().next();
            Quad resultQuad = new Quad(
                    Node.createURI(uriGenerator.nextURI()),
                    firstQuad.getSubject(),
                    firstQuad.getPredicate(),
                    Node.createLiteral(LiteralLabelFactory.create(medianValue)));

            double quality = computeQuality(
                    resultQuad,
                    sourceNamedGraphs,
                    Collections.<String>emptySet(), // disable agree bonus
                    conflictingQuads,
                    metadata,
                    aggregationSpec);
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
            return result;
        }
    }

    /**
     * @see #aggregate()
     * @param conflictingQuads see {@link #aggregate()}
     * @param metadata see {@link #aggregate()}
     * @param aggregationSpec see {@link #aggregate()}
     * @param uriGenerator see {@link #aggregate()}
     * @return see {@link #aggregate()}
     */
    private Collection<CRQuad> aggregateString(
            Collection<Quad> conflictingQuads,
            NamedGraphMetadataMap metadata,
            UniqueURIGenerator uriGenerator,
            AggregationSpec aggregationSpec) {

        Collection<CRQuad> result = createResultCollection();
        ArrayList<String> objects = new ArrayList<String>();
        Collection<String> sourceNamedGraphs = new ArrayList<String>();

        for (Quad quad : conflictingQuads) {
            if (quad.getObject().isLiteral()) {
                objects.add(quad.getObject().getLiteralLexicalForm());
                sourceNamedGraphs.add(quad.getGraphName().getURI());
            } else {
                handleNonAggregableObject(quad, result, aggregationSpec, this.getClass());
            }
        }

        if (sourceNamedGraphs.isEmpty()) {
            return result;
        } else {
            Collections.sort(objects, String.CASE_INSENSITIVE_ORDER);
            int medianPosition = objects.size() / 2;
            String medianValue = objects.get(medianPosition);
            Quad firstQuad = conflictingQuads.iterator().next();
            Quad resultQuad = new Quad(
                    Node.createURI(uriGenerator.nextURI()),
                    firstQuad.getSubject(),
                    firstQuad.getPredicate(),
                    Node.createLiteral(LiteralLabelFactory.create(medianValue)));
            double quality = computeQualityNoAgree(
                    resultQuad,
                    sourceNamedGraphs,
                    conflictingQuads,
                    metadata,
                    aggregationSpec);
            result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
            return result;
        }
    }
}
