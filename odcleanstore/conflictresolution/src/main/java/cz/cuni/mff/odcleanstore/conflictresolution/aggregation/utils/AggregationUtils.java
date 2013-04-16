package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.impl.CalendarLiteralImpl;
import org.openrdf.model.impl.IntegerLiteralImpl;
import org.openrdf.model.impl.NumericLiteralImpl;
import org.openrdf.sail.memory.model.CalendarMemLiteral;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadata;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;
import cz.cuni.mff.odcleanstore.vocabulary.XMLSchema;

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
    private static boolean isUntypedNumericLiteral(Literal literal)  {
        /*if (literal.isWellFormedRaw() && !(literal.getValue() instanceof String)) {
            return false;
        }*/
        return NUMERIC_PATTERN.matcher(literal.stringValue()).matches();
    }


    /**
     * Return the type of a literal node.
     * @see EnumLiteralType
     * @param value a literal node; if not a literal, an IllegalArgumentException is thrown
     * @return type of the given literal
     * TODO: check that it works well with real data
     */
    public static EnumLiteralType getLiteralType(Value value) {
        if (!(value instanceof Literal)) {
            throw new IllegalArgumentException("The given Node must be a literal.");
        }
        Literal literal = (Literal) value;
        String datatypeURI = ODCSUtils.valueToString(literal.getDatatype());
        if (ODCSUtils.isNullOrEmpty(datatypeURI)) {
            return isUntypedNumericLiteral(literal)
                    ? EnumLiteralType.NUMERIC
                    : EnumLiteralType.OTHER;
        } else if (literal instanceof NumericLiteralImpl || literal instanceof IntegerLiteralImpl) {
            //Optimization, test not necessary
            return EnumLiteralType.NUMERIC;
        } else if (literal instanceof CalendarLiteralImpl || literal instanceof CalendarMemLiteral) {
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
    public static double convertToDoubleSilent(Literal literal) {
        try {
            return literal.doubleValue();
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    /**
     * Try to convert a literal node to the numeric value it is representing.
     * If the node is not a literal or not a numeric literal, return Double.NaN instead.
     * @param value a {@link Value}
     * @return double value represented by the given {@link Value}, otherwise Double.NaN
     */
    public static double convertToDoubleSilent(Value value) {
        if (!(value instanceof Literal)) {
            return Double.NaN;
        }
        return convertToDoubleSilent((Literal) value);
    }

    /**
     * Convert the given literal to a boolean value.
     * Only values with lexical form "true", "1" or "yes" are considered true, everything else is false.
     * @param literal a literal
     * @return literal converted to boolean
     */
    public static boolean convertToBoolean(Literal literal) {
        String lexicalForm = literal.stringValue();
        return lexicalForm.equalsIgnoreCase("true") || lexicalForm.equals("1") || lexicalForm.equalsIgnoreCase("yes");
    }

    /**
     * Try to convert a literal node representing a date/time to a {@link XMLGregorianCalendar} instance.
     * If the node is not a date/time literal, return null instead.
     * @param value a {@link Value} to convert
     * @return Date value represented by the given value or null
     */
    public static XMLGregorianCalendar convertToCalendarSilent(Value value) {
        if (!(value instanceof Literal)) {
            return null;
        }
        return getDateTimeValue((Literal) value);
    }

    /**
     * Returns the value of a literal as an XSDDateTime instance if possible, otherwise return null.
     * @param literal a literal representing date/time
     * @return the value of the literal as XSDDateTime or null
     */
    public static XMLGregorianCalendar getDateTimeValue(Literal literal) {
        try {
            return literal.calendarValue();
        } catch (IllegalArgumentException e) {
            return null;
        }
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
    public static int compareByInsertedAt(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        NamedGraphMetadata metadata1 = metadata.getMetadata(quad1.getContext());
        NamedGraphMetadata metadata2 = metadata.getMetadata(quad2.getContext());
        Date insertedAt1 = metadata1 != null ? metadata1.getInsertedAt() : null;
        Date insertedAt2 = metadata2 != null ? metadata2.getInsertedAt() : null;
        return ODCSUtils.nullProofCompare(insertedAt1, insertedAt2);
    }

    /**
     * Returns the literal comparison type for objects of the given quads.
     * If quads contain a quad with a literal object, then returns the type of this  literal, otherwise
     * the comparison cannot be determined and returns null.
     * @param quads collection of (conflicting) quads
     * @return the best comparison type or null
     */
    public static EnumLiteralType getComparisonType(Collection<Statement> quads) {
        for (Statement quad : quads) {
            if (quad.getObject() instanceof Literal) {
                return AggregationUtils.getLiteralType(quad.getObject());
            }
        }
        return null;
    }

    /** Disable constructor for a utility class. */
    private AggregationUtils() {
    }
}
