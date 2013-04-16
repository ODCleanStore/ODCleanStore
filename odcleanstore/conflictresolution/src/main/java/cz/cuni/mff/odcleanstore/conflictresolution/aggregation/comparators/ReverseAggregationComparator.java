package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

/**
 * A comparator that wraps another comparator and reverses its ordering.
 * @author Jan Michelfeit
 */
public class ReverseAggregationComparator implements AggregationComparator {
    private final AggregationComparator baseComparator;

    /**
     * Create a new instance.
     * @param baseComparator comparator that gives the reverse ordering of this instance
     */
    public ReverseAggregationComparator(AggregationComparator baseComparator) {
        this.baseComparator = baseComparator;
    }

    @Override
    public boolean isAggregable(Statement quad) {
        return baseComparator.isAggregable(quad);
    }
    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        return -baseComparator.compare(quad1, quad2, metadata);
    }
}
