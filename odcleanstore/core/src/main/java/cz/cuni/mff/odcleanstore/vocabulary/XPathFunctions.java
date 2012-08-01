package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions for XPathFunctions.
 *
 * @author Jakub Daniel
 */
public final class XPathFunctions {
    /** The namespaspace of the vocabulary as a string. */
    private static final String NS = "http://www.w3.org/2005/xpath-functions:";

    /**
     * Returns the namespaspace of the vocabulary.
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
    public static final String boolFunction = "http://www.w3.org/2005/xpath-functions:boolean";
    public static final String dateFunction = "http://www.w3.org/2005/xpath-functions:date";
    public static final String stringFunction = "http://www.w3.org/2005/xpath-functions:string";
    // CHECKSTYLE:ON
}
