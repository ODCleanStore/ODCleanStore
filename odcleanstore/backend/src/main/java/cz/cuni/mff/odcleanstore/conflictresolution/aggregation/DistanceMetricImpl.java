package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.configuration.ConflictResolutionConfig;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.LevenshteinDistance;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.shared.JenaException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of a distance metric between Node instances.
 * In all methods value 1 means maximum distance, value 0 means identity.
 *
 * @author Jan Michelfeit
 */
/*package*/class DistanceMetricImpl implements DistanceMetric {
    private static final Logger LOG = LoggerFactory.getLogger(DistanceMetricImpl.class);

    /** Minimum distance between two {@link Node Nodes} indicating equal nodes. */
    private static final double MIN_DISTANCE = 0;

    /** Maximum distance between two {@link Node Nodes}. */
    private static final double MAX_DISTANCE = 1;

    /** Distance value for URI resources with different URIs. */
    private static final double DIFFERENT_RESOURCE_DISTANCE = MAX_DISTANCE;

    /** Distance of {@link Node Nodes} of different types. */
    private static final double DIFFERENT_TYPE_DISTANCE = MAX_DISTANCE;

    /** Distance of {@link Node Nodes} when an error (e.g. a parse error) occurs. */
    private static final double ERROR_DISTANCE = MAX_DISTANCE;

    /** Number of seconds in a day. */
    private static final int SECONDS_IN_DAY = (int) (ODCSUtils.DAY_HOURS * ODCSUtils.TIME_UNIT_60 * ODCSUtils.TIME_UNIT_60);

    /** Global configuration values for conflict resolution. */
    private final ConflictResolutionConfig globalConfig;

    /**
     * Creates a new instance.
     * @param globalConfig global configuration values for conflict resolution;
     * values needed in globalConfig are the following:
     * <dl>
     * <dt>getMaxDateDifference
     * <dd>Difference between two dates when their distance is equal to MAX_DISTANCE in seconds.
     * </dl>
     */
    public DistanceMetricImpl(ConflictResolutionConfig globalConfig) {
        this.globalConfig = globalConfig;
    }

    /**
     * {@inheritDoc}
     * @param primaryValue {@inheritDoc }
     * @param comparedValue {@inheritDoc }
     * @return {@inheritDoc }
     */
    @Override
    public double distance(Node primaryValue, Node comparedValue) {
        if (primaryValue.getClass() != comparedValue.getClass()) {
            return DIFFERENT_TYPE_DISTANCE;
        } else if (primaryValue.isURI()) {
            return resourceDistance(primaryValue, comparedValue);
        } else if (primaryValue.isBlank()) {
            return blankNodeDistance(primaryValue, comparedValue);
        } else if (primaryValue.isLiteral()) {
            return literalDistance(primaryValue, comparedValue);
        } else {
            LOG.warn("Distance cannot be measured on Nodes of type {}", primaryValue.getClass().getSimpleName());
            return ERROR_DISTANCE;
        }
    }

    /**
     * Calculates a distance metric between two Node_Literal instances.
     * @see #distance(Node, Node)
     * @param primaryNode first of the compared Nodes; this Node may be considered "referential",
     *        i.e. we measure distance from this value
     * @param comparedNode second of the compared Nodes
     * @return a number from interval [0,1]
     *
     */
    private double literalDistance(Node primaryNode, Node comparedNode) {
        assert primaryNode.isLiteral() && comparedNode.isLiteral();

        EnumLiteralType primaryLiteralType = AggregationUtils.getLiteralType(primaryNode);
        EnumLiteralType comparedLiteralType = AggregationUtils.getLiteralType(comparedNode);
        EnumLiteralType comparisonType = primaryLiteralType == comparedLiteralType
                ? primaryLiteralType
                : EnumLiteralType.OTHER;
        LiteralLabel primaryLiteral = primaryNode.getLiteral();
        LiteralLabel comparedLiteral = comparedNode.getLiteral();

        double result;
        switch (comparisonType) {
        case NUMERIC:
            double primaryValueDouble = AggregationUtils.convertToDoubleSilent(primaryLiteral);
            if (Double.isNaN(primaryValueDouble)) {
                LOG.warn("Numeric literal {} is malformed.", primaryLiteral);
                return ERROR_DISTANCE;
            }
            double comparedValueDouble = AggregationUtils.convertToDoubleSilent(comparedLiteral);
            if (Double.isNaN(comparedValueDouble)) {
                LOG.warn("Numeric literal {} is malformed.", comparedLiteral);
                return ERROR_DISTANCE;
            }
            result = numericDistance(primaryValueDouble, comparedValueDouble);
            break;
        case TIME:
            result = timeDistance(primaryLiteral, comparedLiteral);
            break;
        case DATE:
            result = dateDistance(primaryLiteral, comparedLiteral);
            break;
        case BOOLEAN:
            boolean primaryValueBool = AggregationUtils.convertToBoolean(primaryLiteral);
            boolean comparedValueBool = AggregationUtils.convertToBoolean(comparedLiteral);
            result = primaryValueBool == comparedValueBool ? MIN_DISTANCE : MAX_DISTANCE;
            break;
        case STRING:
        case OTHER:
            result = LevenshteinDistance.normalizedLevenshteinDistance(
                    primaryLiteral.getLexicalForm(),
                    comparedLiteral.getLexicalForm());
            break;
        default:
            LOG.error("Unhandled literal type for comparison {}.", comparisonType);
            throw new RuntimeException("Unhandled literal type for comparison");
        }

        /*LOG.debug("Distance between numeric literals {} and {}: {}",
                new Object[] { primaryNode, comparedNode, result });*/
        return result;
    }

    /**
     * Calculates a distance metric between two numbers.
     * @see #distance(Node, Node)
     * @param primaryValue first of the compared values; this v may be considered "referential",
     *        i.e. we measure distance from this value
     * @param comparedValue second of the compared values
     * @return a number from interval [0,1]
     */
    private double numericDistance(double primaryValue, double comparedValue) {
        double result = primaryValue - comparedValue;
        double average = (primaryValue + comparedValue) / 2;
        if (average != 0) {
            // TODO: document change to normalization with average
            // "Normalize" result to primaryValue;
            // for zero leave as is - the important thing is order of the value
            // which for zero is close enough to 1
            result /= average;
        }
        result = Math.abs(result);
        // result /= SQRT_OF_TWO;
        return Math.min(result, MAX_DISTANCE);
    }

    /**
     * Calculates a distance metric between two time values.
     * The maximum distance is reached with difference of one day.
     * If value types are incompatible or conversion fails, {@value #ERROR_DISTANCE} is returned.
     * @see #distance(Node, Node)
     * @param primaryValue first of the compared values
     * @param comparedValue second of the compared values
     * @return a number from interval [0,1]
     */
    private double timeDistance(LiteralLabel primaryValue, LiteralLabel comparedValue) {
        String primaryDatatypeURI = primaryValue.getDatatypeURI();
        String comparedDatatypeURI = comparedValue.getDatatypeURI();
        if (XMLSchema.timeType.equals(primaryDatatypeURI) && XMLSchema.timeType.equals(comparedDatatypeURI)) {
            try {
                XSDDateTime primaryValueTime = AggregationUtils.getDateTimeValue(primaryValue);
                XSDDateTime comparedValueTime = AggregationUtils.getDateTimeValue(comparedValue);
                if (primaryValueTime == null || comparedValueTime == null) {
                    LOG.warn("Time value '{}' or '{}' is malformed.", primaryValue, comparedValue);
                    return ERROR_DISTANCE;
                }
                double difference = Math.abs(primaryValueTime.getTimePart() - comparedValueTime.getTimePart());
                double result = difference / SECONDS_IN_DAY;
                assert MIN_DISTANCE <= result && result <= MAX_DISTANCE;
                return result;
            } catch (JenaException e) {
                LOG.warn("Time value '{}' or '{}' is malformed.", primaryValue, comparedValue);
                return ERROR_DISTANCE;
            }
        } else {
            LOG.warn("Time literals '{}' and '{}' have incompatible types.", primaryValue, comparedValue);
            return ERROR_DISTANCE;
        }
    }

    /**
     * Calculates a distance metric between two dates.
     * The maximum distance is reached with {@value #MAX_DATE_DIFFERENCE}.
     * If value types are incompatible or conversion fails, {@value #ERROR_DISTANCE} is returned.
     * @see #distance(Node, Node)
     * @param primaryValue first of the compared values
     * @param comparedValue second of the compared values
     * @return a number from interval [0,1]
     */
    private double dateDistance(LiteralLabel primaryValue, LiteralLabel comparedValue) {
        String primaryDatatypeURI = primaryValue.getDatatypeURI();
        String comparedDatatypeURI = comparedValue.getDatatypeURI();
        // CHECKSTYLE:OFF
        if ((XMLSchema.dateTimeType.equals(primaryDatatypeURI) || XMLSchema.dateType.equals(primaryDatatypeURI))
                && (XMLSchema.dateTimeType.equals(comparedDatatypeURI) || XMLSchema.dateType.equals(comparedDatatypeURI))) {
            // CHECKSTYLE:ON
            try {
                XSDDateTime primaryValueTime = AggregationUtils.getDateTimeValue(primaryValue);
                XSDDateTime comparedValueTime = AggregationUtils.getDateTimeValue(comparedValue);
                if (primaryValueTime == null || comparedValueTime == null) {
                    LOG.warn("Date value '{}' or '{}' is malformed.", primaryValue, comparedValue);
                    return ERROR_DISTANCE;
                }
                double differenceInSeconds = Math.abs(primaryValueTime.asCalendar().getTimeInMillis()
                        - comparedValueTime.asCalendar().getTimeInMillis()) / ODCSUtils.MILLISECONDS;
                double result = (MAX_DISTANCE - MIN_DISTANCE)
                        * differenceInSeconds / globalConfig.getMaxDateDifference();
                result = Math.min(result, MAX_DISTANCE);
                assert MIN_DISTANCE <= result && result <= MAX_DISTANCE;
                return result;
            } catch (JenaException e) {
                LOG.warn("Date value '{}' or '{}' is malformed.", primaryValue, comparedValue);
                return ERROR_DISTANCE;
            }
        } else {
            LOG.warn("Date literals '{}' and '{}' have incompatible types.", primaryValue, comparedValue);
            return ERROR_DISTANCE;
        }
    }

    /**
     * Calculates a distance metric between two Node_URI instances.
     * @see #distance(Node, Node)
     * @param primaryValue first of the compared Nodes; this Node may be considered "referential",
     *        i.e. we measure distance from this value
     * @param comparedValue second of the compared Nodes
     * @return a number from interval [0,1]
     *
     */
    private double resourceDistance(Node primaryValue, Node comparedValue) {
        assert primaryValue.isURI() && comparedValue.isURI();
        if (primaryValue.sameValueAs(comparedValue)) {
            return MIN_DISTANCE;
        } else {
            return DIFFERENT_RESOURCE_DISTANCE;
        }
    }

    /**
     * Calculates a distance metric between two Node_URI instances.
     * @see #distance(Node, Node)
     * @param primaryValue first of the compared Nodes; this Node may be considered "referential",
     *        i.e. we measure distance from this value
     * @param comparedValue second of the compared Nodes
     * @return a number from interval [0,1]
     */
    private double blankNodeDistance(Node primaryValue, Node comparedValue) {
        assert primaryValue.isBlank() && comparedValue.isBlank();
        if (primaryValue.sameValueAs(comparedValue)) {
            return MIN_DISTANCE;
        } else {
            return DIFFERENT_RESOURCE_DISTANCE;
        }
    }
}
