package cz.cuni.mff.odcleanstore.vocabulary;

/**
 * Vocabulary definitions for XMLSchema.
 *
 * @author Jan Michelfeit
 */
public final class XMLSchema {
    /** The namespaspace of the vocabulary as a string. */
    private static final String NS = "http://www.w3.org/2001/XMLSchema#";

    /**
     * Returns the namespaspace of the vocabulary.
     * @return namespace of the vocabulary as a string
     */
    public static String getURI() {
        return NS;
    }

    /** Disable constructor for a utility class. */
    private XMLSchema() {
    }

    /* Vocabulary properties: */

    // CHECKSTYLE:OFF
    public static final String stringType = "http://www.w3.org/2001/XMLSchema#string";

    public static final String booleanType = "http://www.w3.org/2001/XMLSchema#boolean";

    public static final String decimalType = "http://www.w3.org/2001/XMLSchema#decimal";

    public static final String floatType = "http://www.w3.org/2001/XMLSchema#float";

    public static final String doubleType = "http://www.w3.org/2001/XMLSchema#double";

    public static final String intType = "http://www.w3.org/2001/XMLSchema#int";

    public static final String integerType = "http://www.w3.org/2001/XMLSchema#integer";

    public static final String longType = "http://www.w3.org/2001/XMLSchema#long";

    public static final String timeType = "http://www.w3.org/2001/XMLSchema#time";

    public static final String dateType = "http://www.w3.org/2001/XMLSchema#date";

    public static final String dateTimeType = "http://www.w3.org/2001/XMLSchema#dateTime";
    // CHECKSTYLE:ON
}
