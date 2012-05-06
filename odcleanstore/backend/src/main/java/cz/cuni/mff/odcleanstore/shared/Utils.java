package cz.cuni.mff.odcleanstore.shared;

import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDAbstractDateTimeType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;

import java.util.regex.Pattern;

/**
 * Various utility methods.
 *
 * @author Jan Michelfeit
 */
public final class Utils {
    //private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[+-]?[0-9]*\\.?[0-9]+$");

    /**
     * Checks if the given literal can be converted to a number (regardless of its datatype).
     * @param literal a literal
     * @return true iff the literal value can be converted to a number
     */
    private static boolean isUntypedNumericLiteral(LiteralLabel literal)  {
        /*if (literal.isWellFormedRaw() && !(literal.getValue() instanceof String)) {
            return false;
        }*/
        return NUMERIC_PATTERN.matcher(literal.getLexicalForm()).matches();
    }

    /**
     * @todo consider using {@link com.hp.hpl.jena.datatypes.RDFDatatype}
     *
     * @param node node a literal node
     * @return type of the given literal
     * TODO: check that it works well with real data
     */
    public static EnumLiteralType getLiteralType(Node node) {
        if (!node.isLiteral()) {
            throw new IllegalArgumentException("The given Node must be a literal.");
        }
        LiteralLabel literal = node.getLiteral();
        String datatypeURI = literal.getDatatypeURI();
        if (Utils.isNullOrEmpty(datatypeURI)) {
            return isUntypedNumericLiteral(literal)
                    ? EnumLiteralType.NUMERIC
                    : EnumLiteralType.OTHER;
        } else if (literal.isWellFormedRaw() && literal.getValue() instanceof Number) {
            // Optimization, test not necessary
            return EnumLiteralType.NUMERIC;
        } else if (literal.isWellFormed() && literal.getDatatype() instanceof XSDAbstractDateTimeType) {
            // Optimization, test not necessary
            return datatypeURI.equals(XMLSchema.timeType) ? EnumLiteralType.TIME : EnumLiteralType.DATE;
        } else if (datatypeURI.equals(XMLSchema.booleanType)) {
            return EnumLiteralType.BOOLEAN;
        } else if (datatypeURI.equals(XMLSchema.stringType)) {
            return EnumLiteralType.STRING;
        } else if (datatypeURI.equals(XMLSchema.dateTimeType)
                || datatypeURI.equals(XMLSchema.dateType)) {
            return EnumLiteralType.DATE;
        } else if (datatypeURI.equals(XMLSchema.timeType)) {
            return EnumLiteralType.TIME;
        } else if (datatypeURI.equals(XMLSchema.integerType)
                || datatypeURI.equals(XMLSchema.intType)
                || datatypeURI.equals(XMLSchema.longType)
                || datatypeURI.equals(XMLSchema.decimalType)
                || datatypeURI.equals(XMLSchema.floatType)
                || datatypeURI.equals(XMLSchema.doubleType)) {
            return EnumLiteralType.NUMERIC;
        } else {
            return isUntypedNumericLiteral(literal)
                    ? EnumLiteralType.NUMERIC
                    : EnumLiteralType.OTHER;
        }
    }

    /**
     * Try to convert a literal to the numeric value it is representing.
     * If the node is not a numeric literal, return Double.NaN instead.
     * @param literal a literal value
     * @return double value represented by the given literal, otherwise Double.NaN
     */
    public static double convertToDoubleSilent(LiteralLabel literal) {
        try {
            Object value = literal.getValue();
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else {
                return Double.parseDouble(literal.getLexicalForm());
            }
        } catch (DatatypeFormatException e) {
            return Double.NaN;
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    /**
     * Try to convert a literal node to the numeric value it is representing.
     * If the node is not a literal or not a numeric literal, return Double.NaN instead.
     * @param node a {@link Node}
     * @return double value represented by the given Node, otherwise Double.NaN
     */
    public static double convertToDoubleSilent(Node node) {
        if (!node.isLiteral()) {
            return Double.NaN;
        }
        return convertToDoubleSilent(node.getLiteral());
    }

    /**
     * Convert the given literal to a boolean value.
     * Only values with lexical form "true", "1" or "yes" are considered true, everything else is false.
     * @param literal a literal
     * @return literal converted to boolean
     */
    public static boolean convertToBoolean(LiteralLabel literal) {
        String lexicalForm = literal.getLexicalForm();
        return lexicalForm.equalsIgnoreCase("true") || lexicalForm.equals("1") || lexicalForm.equalsIgnoreCase("yes");
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
