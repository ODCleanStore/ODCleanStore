package cz.cuni.mff.odcleanstore.shared;

import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.graph.Node;

/**
 * Various utility methods.
 *
 * @author Jan Michelfeit
 */
public final class Utils {
    /**
     * @todo consider using {@link com.hp.hpl.jena.datatypes.RDFDatatype}
     *
     * @param node node a literal node
     * @return type of the given literal
     */
    public static EnumLiteralType getLiteralType(Node node) {
        assert node.isLiteral();
        String datatypeURI = node.getLiteralDatatypeURI();
        if (Utils.isNullOrEmpty(datatypeURI)) {
            return EnumLiteralType.OTHER;
        } else if (datatypeURI.equals(XMLSchema.stringType)) {
            return EnumLiteralType.STRING;
        } else if (datatypeURI.equals(XMLSchema.decimalType)
                || datatypeURI.equals(XMLSchema.intType)
                || datatypeURI.equals(XMLSchema.floatType)
                || datatypeURI.equals(XMLSchema.doubleType)) {
            return EnumLiteralType.NUMERIC;
        } else if (datatypeURI.equals(XMLSchema.booleanType)) {
            return EnumLiteralType.BOOLEAN;
        } else if (datatypeURI.equals(XMLSchema.dateType)
                || datatypeURI.equals(XMLSchema.timeType)) {
            return EnumLiteralType.DATE;
        } else {
            return EnumLiteralType.OTHER;
        }

    }

    /**
     * Checks if a string is null or an empty string.
     * @param s tested string
     * @return true iff s is null or an empty string
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /** Disable constructor for a utility class. */
    private Utils() {
    }
}
