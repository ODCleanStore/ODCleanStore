package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Definitions of RDFS vocabulary.
 *
 * @author Jan Michelfeit
 */
public final class RDFS {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private RDFS() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF
    public static final String label = NS + "label";

    // CHECKSTYLE:ON
}
