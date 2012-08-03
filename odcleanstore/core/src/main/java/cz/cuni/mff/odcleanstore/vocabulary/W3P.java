package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions of W3p provenance model for the Web.
 *
 * @author Jan Michelfeit
 */
public final class W3P {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://purl.org/provenance#";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private W3P() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF

    /**
     * Publisher of data.
     */
    public static final String publishedBy = "http://purl.org/provenance#publishedBy";

    /**
     * A SourcePage is a source (page) of the given ExtractedArtifact.
     */
    public static final String source = "http://purl.org/provenance#source";

    /**
     * An Artifact was inserted to the data store by a User.
     */
    public static final String insertedBy = "http://purl.org/provenance#insertedBy";

    /**
     * The data was inserted at.
     */
    public static final String insertedAt = "http://purl.org/provenance#insertedAt";
    
    // CHECKSTYLE:ON
}
