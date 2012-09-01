package cz.cuni.mff.odcleanstore.shared;

import java.util.regex.Pattern;



/**
 * Various utility methods.
 *
 * @author Jan Michelfeit
 */
public final class Utils {
    /* Simplified patterns for IRIs and prefixed names  based on
     * specification at http://www.w3.org/TR/rdf-sparql-query/#QSynIRI
     */
    private static final String PN_CHARS_BASE = "A-Za-z\\xC0-\\xFF";
    private static final String PN_CHARS_U = PN_CHARS_BASE + "_";
    private static final String PN_CHARS = PN_CHARS_U + "\\-0-9\\xB7";
    private static final String PN_PREFIX_PATTERN =
            "[" + PN_CHARS_BASE + "](?:[" + PN_CHARS + ".]*[" + PN_CHARS + "])?";
    private static final String PN_LOCAL_PATTERN =
            "[" + PN_CHARS_U + "0-9](?:[" + PN_CHARS + ".]*[" + PN_CHARS + "])?";

    private static final Pattern IRI_PATTERN = Pattern.compile("^[^<>\"{}|^`\\x00-\\x20]*$");
    private static final Pattern PREFIXED_NAME_PATTERN =
            Pattern.compile("^(" + PN_PREFIX_PATTERN + ")?:(" + PN_LOCAL_PATTERN + ")?$");

    /** Milliseconds in a second. */
    public static final long MILLISECONDS = 1000;
    
    /** Time unit 60. */
    public static final long TIME_UNIT_60 = 60;
    
    /** Number of hours in a day. */
    public static final long DAY_HOURS = 24;
    
    /** Jdbc driver class. */
    public static final String JDBC_DRIVER = "virtuoso.jdbc4.Driver";

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

    /**
     * Checks whether the given URI is a valid IRI.
     * See http://www.w3.org/TR/rdf-sparql-query/#QSynIRI
     * @param uri the string to check
     * @return true iff the given string is a valid IRI
     */
    public static boolean isValidIRI(String uri) {
        return !uri.isEmpty() && IRI_PATTERN.matcher(uri).matches();
    }

    /**
     * Checks whether the given URI is a prefixed name.
     * See http://www.w3.org/TR/rdf-sparql-query/#QSynIRI.
     * @param uri the string to check
     * @return true iff the given string is a valid IRI
     */
    public static boolean isPrefixedName(String uri) {
        return !uri.isEmpty() && PREFIXED_NAME_PATTERN.matcher(uri).matches();
    }
    
    /**
     * Checks if a string is null or an empty string.
     * @param s tested string
     * @return true iff s is null or an empty string
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /** Disable constructor for a utility class. */
    private Utils() {
    }
}
