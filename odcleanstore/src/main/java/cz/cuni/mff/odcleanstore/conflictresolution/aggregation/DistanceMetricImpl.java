package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import com.hp.hpl.jena.graph.Node;

/**
 * The default implementation of a distance metric between TripleItems.
 * In all methods value 1 means maximum distance, value 0 means identity.
 *
 * @author Jan Michelfeit
 */
class DistanceMetricImpl implements DistanceMetric {
    /** Distance value for URI resources with different URIs. */
    private static final double DIFFERENT_RESOURCE_DISTANCE = 1;

    /** Distance of {@link Node Nodes} of different types. */
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
    public double distance(Node primaryValue, Node comparedValue) {
        if (primaryValue.getClass() != comparedValue.getClass()) {
            return DIFFERENT_TYPE_DISTANCE;
        } else if (primaryValue.isURI()) {
            return resourceDistance(primaryValue, comparedValue);
        } else if (primaryValue.isLiteral()) {
            return LevenstheinDistance.computeNormalizedLevenshteinDistance(
                    primaryValue.getLiteralLexicalForm(),
                    comparedValue.getLiteralLexicalForm());
        }
        // TODO: blank nodes etc
        throw new IllegalArgumentException("Unknown type of TripleItem.");
    }

    /**
     * Calulates a distance metric between two numbers.
     * @see #distance(Node, Node)
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
     * @todo choose an algorithm
     *       Calulates a distance metric between two strings.
     * @see #distance(Node, Node)
     * @todo length limitation for comparison
     */
    private double stringDistance(String primaryValue, String comparedValue) {
        return LevenstheinDistance.computeNormalizedLevenshteinDistance(
                primaryValue, comparedValue);
    }

    /**
     * Calulates a distance metric between two Node_URI instances.
     * @see #distance(Node, Node)
     */
    private double resourceDistance(Node primaryResource, Node comparedResource) {
        assert primaryResource.isURI() && comparedResource.isURI();
        if (primaryResource.sameValueAs(comparedResource)) {
            return 0;
        } else {
            return DIFFERENT_RESOURCE_DISTANCE;
        }
    }

}
