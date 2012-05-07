package cz.cuni.mff.odcleanstore.shared;


/**
 * Various utility methods.
 *
 * @author Jan Michelfeit
 */
public final class Utils {
    /**
     * Compare two values which may be null. Null is considered less than all non-null values.
     * @param o1 first compared value or null
     * @param o2 second compared value or null
     * @param <T> type of compared values
     * @return a negative integer, zero, or a positive integer as o1 is less than, equal to, or greater than o2
     */
    public static <T> int nullProofCompare(Comparable<T> o1, T o2) {
        if (o1 != null && o2 != null) {
            return o1.compareTo(o2);
        } else if (o1 != null) {
            return 1;
        } else if (o2 != null) {
            return -1;
        } else {
            assert o1 == null && o2 == null;
            return 0;
        }
    }

    /** Disable constructor for a utility class. */
    private Utils() {
    }
}
