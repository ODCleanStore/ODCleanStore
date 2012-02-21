package cz.cuni.mff.odcleanstore.conflictresolution.aggregation;

import cz.cuni.mff.odcleanstore.graph.TripleItem;

/**
 * A comparison metric that returns a distance between to TripleItems
 * as a number from interval [0,1].
 * 
 * @author Jan Michelfeit
 */
interface DistanceMetric {
    /**
     * Computes distance between two TripleItems as a number from [0,1].
     * Value 1 means maximum distance, value 0 means identity.
     * @param primaryValue first of the compared TripleItems; this
     *      TripleItem may be considered "referential", i.e. we measure distance
     *      from this value
     * @param comparedValue second of the compared TripleItems
     * @return a number from interval [0,1]
     */
    double distance(TripleItem primaryValue, TripleItem comparedValue);
}
