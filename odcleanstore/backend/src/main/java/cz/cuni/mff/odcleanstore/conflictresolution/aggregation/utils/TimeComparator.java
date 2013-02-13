package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils;

import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

import java.util.Calendar;
import java.util.Comparator;

/**
 * Compares two dates objects based on the time part only.
 * Implemented as singleton.
 * @author Jan Michelfeit
 */
public final class TimeComparator implements Comparator<Calendar> {
    private static final long MILLIS_IN_DAY =
            ODCSUtils.DAY_HOURS * ODCSUtils.TIME_UNIT_60 * ODCSUtils.TIME_UNIT_60 * ODCSUtils.MILLISECONDS;

    private static final TimeComparator INSTANCE = new TimeComparator();

    @Override
    public int compare(Calendar o1, Calendar o2) {
        return (int) ((o1.getTimeInMillis() % MILLIS_IN_DAY) - (o2.getTimeInMillis() % MILLIS_IN_DAY));
    }

    /**
     * Same as {@link #compare(Calendar, Calendar)} but null proof.
     * @see cz.cuni.mff.odcleanstore.shared.Utils.nullProofCompare()
     * @param o1 first compared object
     * @param o2 second compared object
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to,
     *         or greater than the second.
     */
    public int nullProofCompare(Calendar o1, Calendar o2) {
        if (o1 != null && o2 != null) {
            return compare(o1, o2);
        } else if (o1 != null) {
            return 1;
        } else if (o2 != null) {
            return -1;
        } else {
            assert o1 == null && o2 == null;
            return 0;
        }
    }

    /**
     * Singleton - hide constructor.
     */
    private TimeComparator() {
    }

    /**
     * Get the singleton instance.
     * @return the singleton instance.
     */
    public static TimeComparator getInstance() {
        return INSTANCE;
    }
}
