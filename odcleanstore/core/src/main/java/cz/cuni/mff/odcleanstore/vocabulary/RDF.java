package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Definitions of RDF vocabulary.
 *
 * @author Jan Michelfeit
 */
public final class RDF {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private RDF() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF
    /** The subject is an instance of a class. */
    public static final String type = NS + "type";

    // CHECKSTYLE:ON
}
