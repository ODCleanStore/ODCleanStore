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
    public static final String stringType = NS + "string";

    public static final String booleanType = NS + "boolean";

    public static final String decimalType = NS + "decimal";

    public static final String floatType = NS + "float";

    public static final String doubleType = NS + "double";

    public static final String intType = NS + "int";

    public static final String integerType = NS + "integer";

    public static final String longType = NS + "long";

    public static final String timeType = NS + "time";

    public static final String dateType = NS + "date";

    public static final String dateTimeType = NS + "dateTime";
    
    public static final String gYear = NS + "gYear";
    // CHECKSTYLE:ON
}
