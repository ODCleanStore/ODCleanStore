/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.MediatingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.TimeComparator;

/**
 * Chooses the statement with median value of its object among objects of all quads to be resolved.
 * @author Jan Michelfeit
 */
public class MedianResolution extends MediatingResolutionFunction {
    private  static final String FUNCTION_NAME = "MEDIAN";
    
    /**
     * Returns a string identifier of this resolution function ({@value #FUNCTION_NAME}) - can be used to 
     * retrieve the resolution function from the default initialized 
     * {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolutionFunctionRegistry}.
     * @see cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolverFactory#createInitializedResolutionFunctionRegistry()
     * @return string identifier of this resolution function
     */
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(MedianResolution.class);
    private static final int INITIAL_RESULT_CAPACITY = 5; // expect few non-aggregable statements

    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    public MedianResolution(MediatingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    public Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
        // Avoid sorting when only zero or one statements are resolved
        if (statements.isEmpty()) {
            return Collections.emptySet();
        } else if (statements.size() == 1) {
            Statement first = statements.iterator().next();
            Set<Resource> sources = filterSources(first, statements);
            double fQuality = getFQuality(first.getObject(), statements, sources, crContext);
            ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                    first.getSubject(),
                    first.getPredicate(),
                    first.getObject(),
                    fQuality,
                    sources);
            return Collections.singleton(resolvedStatement);
        }

        EnumLiteralType comparisonType = ResolutionFunctionUtils.getComparisonType(statements);
        if (comparisonType == null) {
            // Undefined comparison, treat all statements as non-aggregable
            Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(statements.size());
            for (Statement statement : statements) {
                handleNonAggregableStatement(statement, statements, crContext, result);
            }
            return result;
        } else {
            // Find median
            return getImpl(comparisonType).resolve(statements, crContext);
        }
    }

    /**
     * Choose and return the appropriate aggregation implementation for the given comparison type.
     * Instances for each implementation types are cached.
     * @param comparisonType type of comparison for sorting values
     * @return actual implementation of median aggregation for the given comparison type
     */
    private MedianResolutionImpl<?> getImpl(EnumLiteralType comparisonType) {
        switch (comparisonType) {
        case NUMERIC:
            return new NumericMedianResolution();
        case DATE_TIME:
            return new DateMedianResolution();
        case TIME:
            return new TimeMedianResolution();
        case BOOLEAN:
            return new BooleanMedianResolution();
        case STRING:
        case OTHER:
            return new StringMedianResolution();
        default:
            LOG.error("Unhandled type of literal {} in {}.", comparisonType.name(), this.getClass().getSimpleName());
            throw new RuntimeException("Unhandled type of literal");
        }
    }

    /**
     * Base class for implementations of the median aggregation for various types of literals.
     * @param <T> type of aggregated (object) values
     */
    private abstract class MedianResolutionImpl<T> {
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
         * @param valueFactory
         * @return value converted to {@link Literal}
         */
        protected abstract Literal createLiteral(T value, ValueFactory valueFactory);

        /**
         * Implementation of {@link MedianAggegation#resolve(Collection, _NamedGraphMetadataMap)} for a specific
         * comparison type.
         * @param conflictingQuads see {@link MedianAggegation#resolve(Collection, _NamedGraphMetadataMap)}
         * @param metadata see {@link MedianAggegation#resolve(Collection, _NamedGraphMetadataMap)}
         * @return see {@link MedianAggegation#resolve(Collection, _NamedGraphMetadataMap)}
         */
        public final Collection<ResolvedStatement> resolve(Model statements, CRContext crContext) {
            Collection<ResolvedStatement> result = new ArrayList<ResolvedStatement>(INITIAL_RESULT_CAPACITY);
            ArrayList<T> objects = new ArrayList<T>(statements.size());
            Set<Resource> sources = new HashSet<Resource>(statements.size());
            Statement lastConvertible = null;

            for (Statement statement : statements) {
                T value = getValue(statement.getObject());
                if (value != null) {
                    objects.add(value);
                    sources.add(statement.getContext());
                    lastConvertible = statement;
                } else {
                    handleNonAggregableStatement(statement, statements, crContext, result);
                }
            }

            // Create result
            if (lastConvertible == null) {
                // No convertible values
                return result;
            } else {
                sortValues(objects);
                int medianPosition = objects.size() / 2;
                T medianValue = objects.get(medianPosition);
                Value object = createLiteral(medianValue, crContext.getResolvedStatementFactory().getValueFactory());

                double fQuality = getFQuality(object, statements, sources, crContext);
                ResolvedStatement resolvedStatement = crContext.getResolvedStatementFactory().create(
                        lastConvertible.getSubject(),
                        lastConvertible.getPredicate(),
                        object,
                        fQuality,
                        sources);
                result.add(resolvedStatement);
                return result;
            }
        }
    }

    /**
     * Implementation of median aggregation for numeric values.
     */
    private final class NumericMedianResolution extends MedianResolutionImpl<Double> {
        @Override
        protected Double getValue(Value object) {
            Double numberValue = ResolutionFunctionUtils.convertToDoubleSilent(object);
            return numberValue.isNaN() ? null : numberValue;
        }

        @Override
        protected void sortValues(ArrayList<Double> values) {
            Collections.sort(values);
        }

        @Override
        protected Literal createLiteral(Double value, ValueFactory valueFactory) {
            return valueFactory.createLiteral((double) value);
        }
    }

    /**
     * Implementation of median aggregation for string values.
     */
    private final class StringMedianResolution extends MedianResolutionImpl<String> {
        @Override
        protected String getValue(Value object) {
            return object instanceof Literal ? object.toString() : null;
        }

        @Override
        protected void sortValues(ArrayList<String> values) {
            Collections.sort(values, String.CASE_INSENSITIVE_ORDER);
        }

        @Override
        protected Literal createLiteral(String value, ValueFactory valueFactory) {
            return valueFactory.createLiteral(value);
        }
    }

    /**
     * Implementation of median aggregation for boolean values.
     */
    private final class BooleanMedianResolution extends MedianResolutionImpl<Boolean> {
        @Override
        protected Boolean getValue(Value object) {
            return object instanceof Literal
                    ? ResolutionFunctionUtils.convertToBoolean((Literal) object)
                    : null;
        }

        @Override
        protected void sortValues(ArrayList<Boolean> values) {
            Collections.sort(values);
        }

        @Override
        protected Literal createLiteral(Boolean value, ValueFactory valueFactory) {
            return valueFactory.createLiteral((boolean) value);
        }
    }

    /**
     * Implementation of median aggregation for date values.
     */
    private final class DateMedianResolution extends MedianResolutionImpl<Calendar> {
        @Override
        protected Calendar getValue(Value object) {
            XMLGregorianCalendar calendar = ResolutionFunctionUtils.convertToCalendarSilent(object);
            return calendar != null ? calendar.toGregorianCalendar() : null;
        }

        @Override
        protected void sortValues(ArrayList<Calendar> values) {
            Collections.sort(values);
        }

        @Override
        protected Literal createLiteral(Calendar value, ValueFactory valueFactory) {
            return valueFactory.createLiteral(value.getTime());
        }
    }

    /**
     * Implementation of median aggregation for time values.
     */
    private final class TimeMedianResolution extends MedianResolutionImpl<Calendar> {
        @Override
        protected Calendar getValue(Value object) {
            XMLGregorianCalendar calendar = ResolutionFunctionUtils.convertToCalendarSilent(object);
            return calendar != null ? calendar.toGregorianCalendar() : null;
        }

        @Override
        protected void sortValues(ArrayList<Calendar> values) {
            Collections.sort(values, TimeComparator.getInstance());
        }

        @Override
        protected Literal createLiteral(Calendar value, ValueFactory valueFactory) {
            return valueFactory.createLiteral(value.getTime());
        }
    }
}
