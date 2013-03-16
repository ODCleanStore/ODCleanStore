package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;

/**
 * Helper interface that provides access to a total quality computation of a named graph.
 * @author Jan Michelfeit
 */
public interface GraphQualityCalculator {
    /**
     * Returns the total calculated quality of a source of a named graph.
     * @param metadata metadata of the estimated named graph (or null if unknown)
     * @return quality of source of the named graph as a value from [0,1]
     */
    double getSourceQuality(NamedGraphMetadata metadata);
}
