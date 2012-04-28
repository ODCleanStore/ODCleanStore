package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions of ODCleanStore-specific properties and classes.
 *
 * @todo update according to Glossary
 * @author Jan Michelfeit
 */
public final class ODCS {
    /** The namespace of the vocabulary as a string. */
    private static final String NS = "http://opendata.cz/infrastructure/odcleanstore/";

    /**
     * Returns the namespace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private ODCS() {
    }

    /* Vocabulary properties: */

    /* TODO: return Node_URI instead of String? */

    // CHECKSTYLE:OFF

    /**
     * Property linking a named graph to its error localization score.
     */
    public static final String score = "http://opendata.cz/infrastructure/odcleanstore/score";

    /**
     * Property linking a publisher to her aggregated score.
     */
    public static final String publisherScore = "http://opendata.cz/infrastructure/odcleanstore/publisherScore";

    /**
     * Property linking a named graph to its conflict resolution quality estimate.
     */
    public static final String quality = "http://opendata.cz/infrastructure/odcleanstore/quality";

    // CHECKSTYLE:ON
}
