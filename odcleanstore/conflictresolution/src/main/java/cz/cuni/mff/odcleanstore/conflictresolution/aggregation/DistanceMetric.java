package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import org.openrdf.model.Value;

/**
 * A comparison metric that returns a distance between two {@link Value Values} as a number from
 * interval [0,1].
 *
 * @author Jan Michelfeit
 */
/*package*/interface DistanceMetric {
    /**
     * Computes distance between two {@link Value Values} as a number from [0,1].
     * Value 1 means maximum distance, value 0 means identity.
     * @param primaryValue first of the compared Nodes; this Node may be considered "referential",
     *        i.e. we measure distance from this value
     * @param comparedValue second of the compared Nodes
     * @return a number from interval [0,1]
     */
    double distance(Value primaryValue, Value comparedValue);
}
