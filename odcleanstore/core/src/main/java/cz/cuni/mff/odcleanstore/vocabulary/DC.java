package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * DCMI Metadata Terms vocabulary definitions.
 *
 * @author Jan Michelfeit
 */
public final class DC {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://purl.org/dc/terms/";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private DC() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF

    /**
     * A legal document giving official permission to do something with the resource.
     */
    public static final String license = "http://purl.org/dc/terms/license";
    
    /**
     * A name given to the resource.
     */
    public static final String title = "http://purl.org/dc/terms/title";
    
    /**
     * A point or period of time associated with an event in the lifecycle of the resource.
     */
    public static final String date = "http://purl.org/dc/terms/date";

    // CHECKSTYLE:ON
}
