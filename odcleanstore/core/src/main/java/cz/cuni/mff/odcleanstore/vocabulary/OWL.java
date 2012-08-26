package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions for OWL.
 *
 * @author Jan Michelfeit
 */
public final class OWL {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://www.w3.org/2002/07/owl#";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private OWL() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF
    public static final String sameAs = NS + "sameAs";

    public static final String maxCardinality = NS + "maxCardinality";

    public static final String minCardinality = NS + "minCardinality";

    public static final String cardinality = NS + "cardinality";
    // CHECKSTYLE:ON
}
