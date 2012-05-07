package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * A comparator that wraps another comparator and reverses its ordering.
 * @author Jan Michelfeit
 */
public class ReverseAggregationComparator implements AggregationComparator {
    private AggregationComparator baseComparator;

    /**
     * Create a new instance.
     * @param baseComparator comparator that gives the reverse ordering of this instance
     */
    public ReverseAggregationComparator(AggregationComparator baseComparator) {
        this.baseComparator = baseComparator;
    }

    @Override
    public boolean isAggregable(Quad quad) {
        return baseComparator.isAggregable(quad);
    }
    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        return -baseComparator.compare(quad1, quad2, metadata);
    }
}
