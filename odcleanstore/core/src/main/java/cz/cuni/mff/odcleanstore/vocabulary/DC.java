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
    public static final String license = NS + "license";
    
    /**
     * A name given to the resource.
     */
    public static final String title = NS + "title";
    
    /**
     * Description.
     */
    public static final String description = NS + "description";
    
    /**
     * A point or period of time associated with an event in the lifecycle of the resource.
     */
    public static final String date = NS + "date";

    // CHECKSTYLE:ON
}
