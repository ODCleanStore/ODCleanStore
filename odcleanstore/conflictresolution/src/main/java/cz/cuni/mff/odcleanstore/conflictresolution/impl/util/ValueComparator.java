package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import java.util.Comparator;

import org.openrdf.model.Value;

/**
 * A comparator of {@link Value Values} for purposes of Conflict Resolution.
 * All CR implementation classes should use this comparator for consistency.
 * @author Jan Michelfeit
 */
public class ValueComparator implements Comparator<Value> {
    @Override
    public int compare(Value o1, Value o2) {
        return CRUtils.compareValues(o1, o2);
    }
}
