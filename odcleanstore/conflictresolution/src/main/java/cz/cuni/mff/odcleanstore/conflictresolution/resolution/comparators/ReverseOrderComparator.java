package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * A comparator that wraps another comparator and reverses its ordering.
 * @author Jan Michelfeit
 */
public class ReverseOrderComparator implements BestSelectedLiteralComparator {
    private final BestSelectedComparator<Value> baseComparator;

    /**
     * Create a new instance.
     * @param baseComparator comparator that gives the reverse ordering of this instance
     */
    public ReverseOrderComparator(BestSelectedComparator<Value> baseComparator) {
        this.baseComparator = baseComparator;
    }

    @Override
    public boolean accept(Value object, CRContext crContext) {
        return baseComparator.accept(object, crContext);
    }

    @Override
    public int compare(Value o1, Value o2, CRContext crContext) {
        return -baseComparator.compare(o1, o2, crContext);
    }
}
