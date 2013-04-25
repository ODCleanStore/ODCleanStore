package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * A comparator that wraps another comparator and reverses its ordering.
 * @author Jan Michelfeit
 */
public class ReverseOrderComparator implements LiteralComparator {
    private final LiteralComparator baseComparator;

    /**
     * Create a new instance.
     * @param baseComparator comparator that gives the reverse ordering of this instance
     */
    public ReverseOrderComparator(LiteralComparator baseComparator) {
        this.baseComparator = baseComparator;
    }

    @Override
    public boolean accept(Value object, CRContext crContext) {
        return baseComparator.accept(object, crContext);
    }

    @Override
    public int compare(Value object1, Value object2) {
        return -baseComparator.compare(object1, object2);
    }
}
