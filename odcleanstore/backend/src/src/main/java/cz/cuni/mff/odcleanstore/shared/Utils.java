package cz.cuni.mff.odcleanstore.shared;

import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.graph.Node;

import java.util.regex.Pattern;

/**
 * Various utility methods.
 *
 * @author Jan Michelfeit
 */
public final class Utils {
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[+-]?[0-9]*\\.?[0-9]+$");

    /**
     * Checks if the given literal node can be converted to a number even though it is not
     * neccessarily typed as a numeric literal.
     * @param node a literal node
     * @return true iff the literal value can be converted to string
     */
    private static boolean isUntypedNumericLiteral(Node node)  {
        assert node.isLiteral();
        Object literalValue = node.getLiteralValue();
        if (!(literalValue instanceof String)) {
            return false;
        }
        return NUMERIC_PATTERN.matcher((String) literalValue).matches();
    }

    /**
     * @todo consider using {@link com.hp.hpl.jena.datatypes.RDFDatatype}
     *
     * @param node node a literal node
     * @return type of the given literal
     */
    public static EnumLiteralType getLiteralType(Node node) {
        if (!node.isLiteral()) {
            throw new IllegalArgumentException("The given mode must be a literal.");
        }
        String datatypeURI = node.getLiteralDatatypeURI();
        if (Utils.isNullOrEmpty(datatypeURI)) {
            return isUntypedNumericLiteral(node)
                    ? EnumLiteralType.NUMERIC
                    : EnumLiteralType.OTHER;
        } else if (datatypeURI.equals(XMLSchema.stringType)) {
            return EnumLiteralType.STRING;
        } else if (datatypeURI.equals(XMLSchema.integerType)
                || datatypeURI.equals(XMLSchema.intType)
                || datatypeURI.equals(XMLSchema.longType)
                || datatypeURI.equals(XMLSchema.decimalType)
                || datatypeURI.equals(XMLSchema.floatType)
                || datatypeURI.equals(XMLSchema.doubleType)) {
            return EnumLiteralType.NUMERIC;
        } else if (datatypeURI.equals(XMLSchema.booleanType)) {
            return EnumLiteralType.BOOLEAN;
        } else if (datatypeURI.equals(XMLSchema.dateType)
                || datatypeURI.equals(XMLSchema.timeType)) {
            return EnumLiteralType.DATE;
        } else {
            return isUntypedNumericLiteral(node)
                    ? EnumLiteralType.NUMERIC
                    : EnumLiteralType.OTHER;
        }
    }

    /**
     * Try to convert a literal node to the numeric value its representing.
     * If the node is not a literal or not a numeric literal, return Double.NaN instead.
     * @param node a {@link Node}
     * @return double value represented by the given Node, otherwise Double.NaN
     */
    public static double tryConvertToDouble(Node node) {
        if (!node.isLiteral()) {
            return Double.NaN;
        }
        if (Utils.getLiteralType(node) != EnumLiteralType.NUMERIC) {
            // TODO: optimize, perhaps omit the test?
            return Double.NaN;
        } else if (node.getLiteralValue() instanceof Number) {
            return ((Number) node.getLiteralValue()).doubleValue();
        } else {
            try {
                return Double.parseDouble(node.getLiteralLexicalForm());
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
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
