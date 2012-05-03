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

    private NumericMedianAggregation numericAggregation = null;
    private StringMedianAggregation stringAggregation = null;
    private BooleanMedianAggregation booleanAggregation = null;

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     */
    public MedianAggegation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator) {
        super(aggregationSpec, uriGenerator);
    }

    /**
     * Returns a single quad where the object is the median of objects in conflictingQuads.
     * The type of comparison (e.g. as numbers, strings, dates) is chosen based on the type of
     * the object of the first quad in conflictingQuads. Only literal objects can be aggregated.
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
        if (conflictingQuads.isEmpty()) {
            return result;
        }

        Quad firstQuad = conflictingQuads.iterator().next();
        if (!firstQuad.getObject().isLiteral()) {
            // We need the first object to be a literal because we use it to detect the comparison type
            for (Quad quad : conflictingQuads) {
                handleNonAggregableObject(quad, conflictingQuads, metadata, result, this.getClass());
            }
            return result;
        }

        EnumLiteralType comparisonType = Utils.getLiteralType(firstQuad.getObject());
        return getAggregationImpl(comparisonType).aggregate(conflictingQuads, metadata);
    }

    /**
     * Choose and return the appropriate aggregation implementation for the given comparison type.
     * Instances for each implementation types are cached.
     * @param comparisonType type of comparison for sorting values
     * @return actual implementation of median aggregation for the given comparison type
     */
    private MedianAggregationImpl<?> getAggregationImpl(EnumLiteralType comparisonType) {
        switch (comparisonType) {
        case NUMERIC:
            if (numericAggregation == null) {
                numericAggregation = new NumericMedianAggregation();
            }
            return numericAggregation;
        case DATE:
            // TODO
        case BOOLEAN:
            if (booleanAggregation == null) {
                booleanAggregation = new BooleanMedianAggregation();
            }
            return booleanAggregation;
        case STRING:
        case OTHER:
            if (stringAggregation == null) {
                stringAggregation = new StringMedianAggregation();
            }
            return stringAggregation;
        default:
            LOG.error("Unhandled type of literal {} in {}.", comparisonType.name(), this.getClass().getSimpleName());
            throw new RuntimeException("Unhandled type of literal");
        }
    }

    /**
     * Base class for implementations of the median aggregation for various types of literals.
     * @param <T> type of aggregated (object) values
     */
    private abstract class MedianAggregationImpl<T> {
        /**
         * Return the value of an object node converted to the given type, or null if the object is non-aggregable
         * for this type of aggregation.
         * @param object object of an aggregated quad
         * @return object value converted to T or null if the object is non-aggregable
         */
        protected abstract T getValue(Node object);

        /**
         * Sort values in the given list.
         * @param values a list to sort
         */
        protected abstract void sortValues(ArrayList<T> values);

        /**
         * Implementation of {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)} for a specific
         * comparison type.
         * @param conflictingQuads see {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)}
         * @param metadata see {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)}
         * @return see {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)}
         */
        public final Collection<CRQuad> aggregate(Collection<Quad> conflictingQuads, NamedGraphMetadataMap metadata) {
            Collection<CRQuad> result = createResultCollection();
            ArrayList<T> objects = new ArrayList<T>(conflictingQuads.size());
            Collection<Quad> aggregableQuads = new ArrayList<Quad>(conflictingQuads.size());
            Collection<String> sourceNamedGraphs = new ArrayList<String>();

            // Get aggregable quads and their objects converted to double
            for (Quad quad : conflictingQuads) {
                T value = getValue(quad.getObject());
                if (value != null) {
                    objects.add(value);
                    sourceNamedGraphs.add(quad.getGraphName().getURI());
                    aggregableQuads.add(quad);
                } else {
                    handleNonAggregableObject(quad, conflictingQuads, metadata, result, MedianAggegation.class);
                }
            }

            // Create result
            if (aggregableQuads.isEmpty()) {
                return result;
            } else {
                sortValues(objects);
                int medianPosition = objects.size() / 2;
                T medianValue = objects.get(medianPosition);
                Quad firstQuad = aggregableQuads.iterator().next();
                Quad resultQuad = new Quad(
                        Node.createURI(uriGenerator.nextURI()),
                        firstQuad.getSubject(),
                        firstQuad.getPredicate(),
                        Node.createLiteral(LiteralLabelFactory.create(medianValue)));

                double quality = computeQualityNoAgree(
                        resultQuad,
                        sourceNamedGraphs,
                        aggregableQuads,
                        metadata);
                result.add(new CRQuad(resultQuad, quality, sourceNamedGraphs));
                return result;
            }
        }
    }

    /**
     * Implementation of median aggregation for numeric values.
     */
    private final class NumericMedianAggregation extends MedianAggregationImpl<Double> {
        @Override
        protected Double getValue(Node object) {
            Double numberValue = Utils.convertToDoubleSilent(object);
            return numberValue.isNaN() ? null : numberValue;
        }

        @Override
        protected void sortValues(ArrayList<Double> values) {
            Collections.sort(values);
        }
    }

    /**
     * Implementation of median aggregation for string values.
     */
    private final class StringMedianAggregation extends MedianAggregationImpl<String> {
        @Override
        protected String getValue(Node object) {
            return object.isLiteral() ? object.getLiteralLexicalForm() : null;
        }

        @Override
        protected void sortValues(ArrayList<String> values) {
            Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
        }
    }

    /**
     * Implementation of median aggregation for boolean values.
     */
    private final class BooleanMedianAggregation extends MedianAggregationImpl<Boolean> {
        @Override
        protected Boolean getValue(Node object) {
            return object.isLiteral()
                    ? Utils.convertToBoolean(object.getLiteral())
                    : null;
        }

        @Override
        protected void sortValues(ArrayList<Boolean> values) {
            Collections.sort(values);
        }
    }
}
