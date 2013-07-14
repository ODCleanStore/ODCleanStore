package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import java.util.Calendar;
import java.util.Comparator;

import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

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
