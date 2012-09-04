package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions for XPathFunctions.
 *
 * @author Jakub Daniel
 */
public final class XPathFunctions {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://www.w3.org/2005/xpath-functions:";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private XPathFunctions() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF
    public static final String boolFunction = NS + "boolean";
    public static final String dateFunction = NS + "date";
    public static final String stringFunction = NS + "string";
    // CHECKSTYLE:ON
}
