package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.EnumLiteralType;
import cz.cuni.mff.odcleanstore.shared.Utils;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

import com.hp.hpl.jena.datatypes.DatatypeFormatException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.IllegalDateTimeFieldException;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.datatypes.xsd.impl.XSDAbstractDateTimeType;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Aggregation utility methods.
 *
 * @author Jan Michelfeit
 */
public final class AggregationUtils {
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
     * Return the type of a literal node.
     * @see EnumLiteralType
     * @param node node a literal node; if not a literal, an IllegalArgumentException is thrown
     * @return type of the given literal
     * TODO: check that it works well with real data
     */
    public static EnumLiteralType getLiteralType(Node node) {
        if (!node.isLiteral()) {
            throw new IllegalArgumentException("The given Node must be a literal.");
        }
        LiteralLabel literal = node.getLiteral();
        String datatypeURI = literal.getDatatypeURI();
        if (AggregationUtils.isNullOrEmpty(datatypeURI)) {
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
     * Try to convert a literal node representing a date/time to a Calendar instance.
     * If the node is not a date/time literal, return null instead.
     * @param node a {@link Node}
     * @return Date value represented by the given Node or null
     */
    public static Calendar convertToCalendarSilent(Node node) {
        if (!node.isLiteral()) {
            return null;
        }
        XSDDateTime value = getDateTimeValue(node.getLiteral());
        try {
            return value == null ? null : value.asCalendar();
        } catch (IllegalDateTimeFieldException e) {
            return null;
        }
    }

    /**
     * Returns the value of a literal as an XSDDateTime instance if possible, otherwise return null.
     * @param literal a literal representing date/time
     * @return the value of the literal as XSDDateTime or null
     */
    public static XSDDateTime getDateTimeValue(LiteralLabel literal) {
        if (literal.isWellFormed() && literal.getValue() instanceof XSDDateTime) {
            return (XSDDateTime) literal.getValue();
        } else if (literal.getDatatypeURI() != null) {
            try {
                RDFDatatype datatype = TypeMapper.getInstance().getSafeTypeByName(literal.getDatatypeURI());
                Object value = datatype.parse(literal.getLexicalForm());
                if (value instanceof XSDDateTime) {
                    return (XSDDateTime) value;
                }
            } catch (JenaException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Checks if a string is null or an empty string.
     * @param s tested string
     * @return true iff s is null or an empty string
     */
    private static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Compare two quads by their odcs:insertedAt date.
     * A later date is greater than an earlier date, a known date is greater then an unknown (null) date.
     * @param quad1 first compared quad
     * @param quad2 second compared quad
     * @param metadata metadata of the compared quads
     * @return a negative integer, zero, or a positive integer as the first argument
     *      is less than, equal to, or greater than the second
     */
    public static int compareByInsertedAt(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        NamedGraphMetadata metadata1 = metadata.getMetadata(quad1.getGraphName());
        NamedGraphMetadata metadata2 = metadata.getMetadata(quad2.getGraphName());
        Date insertedAt1 = metadata1 != null ? metadata1.getInsertedAt() : null;
        Date insertedAt2 = metadata1 != null ? metadata2.getInsertedAt() : null;
        return Utils.nullProofCompare(insertedAt1, insertedAt2);
    }

    /**
     * Returns the literal comparison type for objects of the given quads.
     * If quads contain a quad with a literal object, then returns the type of this  literal, otherwise
     * the comparison cannot be determined and returns null.
     * @param quads collection of (conflicting) quads
     * @return the best comparison type or null
     */
    public static EnumLiteralType getComparisonType(Collection<Quad> quads) {
        for (Quad quad : quads) {
            if (quad.getObject().isLiteral()) {
                return AggregationUtils.getLiteralType(quad.getObject());
            }
        }
        return null;
    }

    /** Disable constructor for a utility class. */
    private AggregationUtils() {
    }
}
