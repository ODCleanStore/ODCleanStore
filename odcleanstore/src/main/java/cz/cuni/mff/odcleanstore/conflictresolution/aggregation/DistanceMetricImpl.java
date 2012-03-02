package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.graph.LiteralTripleItem;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;

/**
 * The default implementation of a distance metric between TripleItems.
 * In all methods value 1 means maximum distance, value 0 means identity.
 * 
 * @author Jan Michelfeit
 */
class DistanceMetricImpl implements DistanceMetric {
    /** Distance value for URI resources with different URIs. */
    private static final double DIFFERENT_RESOURCE_DISTANCE = 1;

    /** Distance of {@link TripleItem TripleItems} of different types. */
    private static final double DIFFERENT_TYPE_DISTANCE = 1;

    /** Square root of two. */
    private static final double SQRT_OF_TWO = Math.sqrt(2);

    /**
     * {@inheritDoc}
     * @param primaryValue {@inheritDoc }
     * @param comparedValue {@inheritDoc }
     * @return {@inheritDoc }
     * @todo
     */
    @Override
    public double distance(TripleItem primaryValue, TripleItem comparedValue) {
        if (primaryValue.getClass() != comparedValue.getClass()) {
            return DIFFERENT_TYPE_DISTANCE;
        } else if (primaryValue instanceof URITripleItem) {
            return resourceDistance(
                    (URITripleItem) primaryValue,
                    (URITripleItem) comparedValue);
        } else if (primaryValue instanceof LiteralTripleItem) {
            return LevenstheinDistance.computeNormalizedLevenshteinDistance(
                    ((LiteralTripleItem) primaryValue).getValue(),
                    ((LiteralTripleItem) comparedValue).getValue());
        }
        // TODO: blank nodes
        throw new IllegalArgumentException("Unknown type of TripleItem.");
    }

    /**
     * Calulates a distance metric between two numbers.
     * @see #distance(TripleItem, TripleItem)
     */
    private double numericDistance(double primaryValue, double comparedValue) {
        double result = primaryValue - comparedValue;
        if (primaryValue != 0) {
            // "Normalize" result to primaryValue;
            // for zero leave as is - the important thing is order of the value
            // which for zero is close enough to 1
            result /= primaryValue;
        }
        result = Math.abs(result);
        // result /= SQRT_OF_TWO;
        return Math.min(result, 1);
    }

    /**
     * @todo choose algorithm
     *       Calulates a distance metric between two strings.
     * @see #distance(TripleItem, TripleItem)
     * @todo length limitation for comparison
     */
    private double stringDistance(String primaryValue, String comparedValue) {
        return LevenstheinDistance.computeNormalizedLevenshteinDistance(
                primaryValue, comparedValue);
    }

    /**
     * Calulates a distance metric between two URI resources.
     * @see #distance(TripleItem, TripleItem)
     */
    private double resourceDistance(URITripleItem primaryResource, URITripleItem comparedResource) {
        if (primaryResource.getURI().equals(comparedResource.getURI())) {
            return 0;
        } else {
            return DIFFERENT_RESOURCE_DISTANCE;
        }
    }

}
