package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuadImpl;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.TimeComparator;
import cz.cuni.mff.odcleanstore.shared.UniqueURIGenerator;

/**
 * Aggregation method that returns the median of input conflicting triples.
 * This aggregation is applicable only to quads with a literal as their object.
 *
 * "Agree bonus" in quality calculation is not applied for this aggregation.
 *
 * TODO: refactor
 * @author Jan Michelfeit
 */
/*package*/final class MedianAggegation extends CalculatedValueAggregation {
    private static final Logger LOG = LoggerFactory.getLogger(MedianAggegation.class);

    private NumericMedianAggregation numericAggregation = null;
    private DateMedianAggregation dateAggregation = null;
    private TimeMedianAggregation timeAggregation = null;
    private StringMedianAggregation stringAggregation = null;
    private BooleanMedianAggregation booleanAggregation = null;

    /**
     * Creates a new instance with given settings.
     * @param aggregationSpec aggregation and quality calculation settings
     * @param uriGenerator generator of URIs
     * @param distanceMetric a {@link DistanceMetric} used for quality computation
     * @param globalConfig global configuration values for conflict resolution;
     * @see AggregationMethodBase#AggregationMethodBase()
     */
    public MedianAggegation(AggregationSpec aggregationSpec, UniqueURIGenerator uriGenerator,
            DistanceMetric distanceMetric, ConflictResolutionConfig globalConfig) {
        super(aggregationSpec, uriGenerator, distanceMetric, globalConfig);
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
    public Collection<CRQuad> aggregate(Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {
        EnumLiteralType comparisonType = AggregationUtils.getComparisonType(conflictingQuads);
        if (comparisonType == null) {
            Collection<CRQuad> result = createResultCollection();
            for (Statement quad : conflictingQuads) {
                handleNonAggregableObject(quad, conflictingQuads, metadata, result, this.getClass());
            }
            return result;
        } else {
            return getAggregationImpl(comparisonType).aggregate(conflictingQuads, metadata);
        }
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
            if (dateAggregation == null) {
                dateAggregation = new DateMedianAggregation();
            }
            return dateAggregation;
        case TIME:
            if (timeAggregation == null) {
                timeAggregation = new TimeMedianAggregation();
            }
            return timeAggregation;
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
        protected abstract T getValue(Value object);

        /**
         * Sort values in the given list.
         * @param values a list to sort
         */
        protected abstract void sortValues(ArrayList<T> values);

        /**
         * Creates a {@link Literal} for value of type T.
         * Used so that the factory for literals has information about the type of the value
         * @param value value to convert to {@link Literal}
         * @return value converted to {@link Literal}
         */
        protected abstract Literal createLiteral(T value);

        /**
         * Implementation of {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)} for a specific
         * comparison type.
         * @param conflictingQuads see {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)}
         * @param metadata see {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)}
         * @return see {@link MedianAggegation#aggregate(Collection, NamedGraphMetadataMap)}
         */
        public final Collection<CRQuad> aggregate(Collection<Statement> conflictingQuads, NamedGraphMetadataMap metadata) {
            Collection<CRQuad> result = createResultCollection();
            ArrayList<T> objects = new ArrayList<T>(conflictingQuads.size());
            Collection<Statement> aggregableQuads = new ArrayList<Statement>(conflictingQuads.size());
            Collection<String> sourceNamedGraphs = new ArrayList<String>();

            // Get aggregable quads and their objects converted to double
            for (Statement quad : conflictingQuads) {
                T value = getValue(quad.getObject());
                if (value != null) {
                    objects.add(value);
                    sourceNamedGraphs.add(getSourceGraphURI(quad));
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
                Statement firstQuad = aggregableQuads.iterator().next();
                Statement resultQuad = VALUE_FACTORY.createStatement(
                        firstQuad.getSubject(),
                        firstQuad.getPredicate(),
                        createLiteral(medianValue),
                        VALUE_FACTORY.createURI(uriGenerator.nextURI())); 
                double quality = computeQualityNoAgree(
                        resultQuad,
                        sourceNamedGraphs,
                        aggregableQuads,
                        metadata);
                result.add(new CRQuadImpl(resultQuad, quality, sourceNamedGraphs));
                return result;
            }
        }
        
        
    }

    /**
     * Implementation of median aggregation for numeric values.
     */
    private final class NumericMedianAggregation extends MedianAggregationImpl<Double> {
        @Override
        protected Double getValue(Value object) {
            Double numberValue = AggregationUtils.convertToDoubleSilent(object);
            return numberValue.isNaN() ? null : numberValue;
        }

        @Override
        protected void sortValues(ArrayList<Double> values) {
            Collections.sort(values);
        }

        @Override
        protected Literal createLiteral(Double value) {
            return VALUE_FACTORY.createLiteral((double) value);
        }
    }

    /**
     * Implementation of median aggregation for string values.
     */
    private final class StringMedianAggregation extends MedianAggregationImpl<String> {
        @Override
        protected String getValue(Value object) {
            return object instanceof Literal ? object.toString() : null;
        }

        @Override
        protected void sortValues(ArrayList<String> values) {
            Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
        }

        @Override
        protected Literal createLiteral(String value) {
            return VALUE_FACTORY.createLiteral(value);
        }
    }

    /**
     * Implementation of median aggregation for boolean values.
     */
    private final class BooleanMedianAggregation extends MedianAggregationImpl<Boolean> {
        @Override
        protected Boolean getValue(Value object) {
            return object instanceof Literal
                    ? AggregationUtils.convertToBoolean((Literal) object)
                    : null;
        }

        @Override
        protected void sortValues(ArrayList<Boolean> values) {
            Collections.sort(values);
        }

        @Override
        protected Literal createLiteral(Boolean value) {
            return VALUE_FACTORY.createLiteral((boolean) value);
        }
    }

    /**
     * Implementation of median aggregation for date values.
     */
    private final class DateMedianAggregation extends MedianAggregationImpl<Calendar> {
        @Override
        protected Calendar getValue(Value object) {
            XMLGregorianCalendar calendar = AggregationUtils.convertToCalendarSilent(object);
            return calendar != null ? calendar.toGregorianCalendar() : null;
        }

        @Override
        protected void sortValues(ArrayList<Calendar> values) {
            Collections.sort(values);
        }

        @Override
        protected Literal createLiteral(Calendar value) {
            return VALUE_FACTORY.createLiteral(value.getTime());
        }
    }

    /**
     * Implementation of median aggregation for time values.
     */
    private final class TimeMedianAggregation extends MedianAggregationImpl<Calendar> {
        @Override
        protected Calendar getValue(Value object) {
            XMLGregorianCalendar calendar = AggregationUtils.convertToCalendarSilent(object);
            return calendar != null ? calendar.toGregorianCalendar() : null;
        }

        @Override
        protected void sortValues(ArrayList<Calendar> values) {
            Collections.sort(values, TimeComparator.getInstance());
        }
        

        @Override
        protected Literal createLiteral(Calendar value) {
            return VALUE_FACTORY.createLiteral(value.getTime());
        }
    }
}
