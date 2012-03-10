package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.shared.EnumLiteralType;
import cz.cuni.mff.odcleanstore.shared.Utils;

import com.hp.hpl.jena.graph.Node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of a distance metric between Node instances.
 * In all methods value 1 means maximum distance, value 0 means identity.
 *
 * @author Jan Michelfeit
 */
class DistanceMetricImpl implements DistanceMetric {
    private static final Logger LOG = LoggerFactory.getLogger(DistanceMetricImpl.class);

    /** Minimum distance between two {@link Node Nodes} indicating equal nodes. */
    private static final double MIN_DISTANCE = 0;

    /** Maximum distance between two {@link Node Nodes}. */
    private static final double MAX_DISTANCE = 1;

    /** Distance value for URI resources with different URIs. */
    private static final double DIFFERENT_RESOURCE_DISTANCE = MAX_DISTANCE;

    /** Distance of {@link Node Nodes} of different types. */
    private static final double DIFFERENT_TYPE_DISTANCE = MAX_DISTANCE;

    /** Distance of {@link Node Nodes} when an error (e.g. a parse erorr) occurs. */
    private static final double ERROR_DISTANCE = MAX_DISTANCE;

    ///** Square root of two. */
    //private static final double SQRT_OF_TWO = Math.sqrt(2);

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
            LOG.warn("Distance cannot be measured on Nodes of type {}",
                    primaryValue.getClass().getSimpleName());
            return ERROR_DISTANCE;
        }
    }

    /**
     * Calulates a distance metric between two Node_Literal instances.
     * @see #distance(Node, Node)
     * @param primaryNode first of the compared Nodes; this Node may be considered "referential",
     *        i.e. we measure distance from this value
     * @param comparedNode second of the compared Nodes
     * @return a number from interval [0,1]
     *
     */
    private double literalDistance(Node primaryNode, Node comparedNode) {
        assert primaryNode.isLiteral() && comparedNode.isLiteral();

        EnumLiteralType primaryLiteralType = Utils.getLiteralType(primaryNode);
        EnumLiteralType comparedLiteralType = Utils.getLiteralType(comparedNode);
        EnumLiteralType comparisonType = primaryLiteralType == comparedLiteralType
                ? primaryLiteralType
                : EnumLiteralType.OTHER;

        double result;
        switch (comparisonType) {
        case NUMERIC:
            double primaryValue = 0;
            double comparedValue = 0;
            try {
                primaryValue = Double.parseDouble(primaryNode.getLiteralLexicalForm());
            } catch (NumberFormatException e) {
                LOG.warn("Numeric literal {} is malformed.", primaryNode);
                result = ERROR_DISTANCE;
                break;
            }
            try {
                comparedValue = Double.parseDouble(comparedNode.getLiteralLexicalForm());
            } catch (NumberFormatException e) {
                LOG.warn("Numeric literal {} is malformed.", primaryNode);
                result = ERROR_DISTANCE;
                break;
            }
            result = numericDistance(primaryValue, comparedValue);
            break;
        case DATE:
            // TODO
        case BOOLEAN:
            // TODO
        case STRING:
        case OTHER:
            // TODO + check bounds
            result = LevenstheinDistance.computeNormalizedLevenshteinDistance(
                    primaryNode.getLiteralLexicalForm(),
                    comparedNode.getLiteralLexicalForm());
            break;
        default:
            // TODO
            LOG.error("TODO");
            throw new IllegalArgumentException();
        }
        
        LOG.debug("Distance between literals {} and {} of type {}: {}",
                new Object[] { primaryNode, comparedNode, comparedLiteralType, result });
        return result;
    }

    /**
     * Calulates a distance metric between two numbers.
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
     * Calulates a distance metric between two Node_URI instances.
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
     * Calulates a distance metric between two Node_URI instances.
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
