package cz.cuni.mff.odcleanstore;


/**
 * Utility methods for JUnit tests.
 * @author Jan Michelfeit
 */
public final class TestUtils {

    /** Hide constructor for a utility class. */
    private TestUtils() {
    }

    private static long uriCounter = 0;

    /** Returns a URI unique within a test run. @return URI */
    public static String getUniqueURI() {
        uriCounter++;
        return "http://example.com/" + Long.toString(uriCounter);
    }

    /** Resets the URI counter used by {@link #getUniqueURI()}. */
    public static void resetURICounter() {
        uriCounter = 0;
    }
}
