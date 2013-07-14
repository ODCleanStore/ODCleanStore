package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.XMLSchema;

/**
 * Various utility methods for conflict resolution function implementations.
 *
 * @author Jan Michelfeit
 */
public final class ResolutionFunctionUtils {
    //private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[+-]?[0-9]*\\.?[0-9]+$");
    
    private static final Map<URI, EnumLiteralType> LITERAL_TYPES_MAP;
    
    static {
        LITERAL_TYPES_MAP = new HashMap<URI, EnumLiteralType>();
        LITERAL_TYPES_MAP.put(XMLSchema.BOOLEAN, EnumLiteralType.BOOLEAN);
        LITERAL_TYPES_MAP.put(XMLSchema.STRING, EnumLiteralType.STRING);
        LITERAL_TYPES_MAP.put(XMLSchema.TIME, EnumLiteralType.TIME);
        LITERAL_TYPES_MAP.put(XMLSchema.DATE, EnumLiteralType.DATE_TIME);
        LITERAL_TYPES_MAP.put(XMLSchema.DATETIME, EnumLiteralType.DATE_TIME);
        LITERAL_TYPES_MAP.put(XMLSchema.GYEARMONTH, EnumLiteralType.DATE_TIME);
        LITERAL_TYPES_MAP.put(XMLSchema.GYEAR, EnumLiteralType.DATE_TIME);
        LITERAL_TYPES_MAP.put(XMLSchema.INT, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.INTEGER, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.LONG, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.DECIMAL, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.FLOAT, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.DOUBLE, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.BYTE, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.SHORT, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.UNSIGNED_BYTE, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.UNSIGNED_INT, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.UNSIGNED_LONG, EnumLiteralType.NUMERIC);
        LITERAL_TYPES_MAP.put(XMLSchema.UNSIGNED_SHORT, EnumLiteralType.NUMERIC);
    }

    /**
     * Checks if the given literal can be converted to a number (regardless of its datatype).
     * @param literal a literal
     * @return true iff the literal value can be converted to a number
     */
    private static boolean isUntypedNumericLiteral(Literal literal)  {
        return NUMERIC_PATTERN.matcher(literal.stringValue()).matches();
    }

    /**
     * Return the type of a literal node.
     * @see EnumLiteralType
     * @param literal a literal node; if not a literal, an IllegalArgumentException is thrown
     * @return type of the given literal
     * TODO: check that it works well with real data
     */
    public static EnumLiteralType getLiteralType(Literal literal) {
        URI datatype = literal.getDatatype();
        if (datatype == null || datatype.stringValue().isEmpty()) {
            return isUntypedNumericLiteral(literal)
                    ? EnumLiteralType.NUMERIC
                    : EnumLiteralType.STRING; // plain string
        }
        EnumLiteralType lookedUpType = LITERAL_TYPES_MAP.get(datatype);
        return lookedUpType != null ? lookedUpType : EnumLiteralType.OTHER;
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
     * Convert the given {@link Value} to a boolean value.
     * Only literals with lexical form "true", "1" or "yes" are considered true, 
     * everything else (including {@link URI URIs} etc.) is false.
     * @param value an RDF node
     * @return literal converted to boolean
     */
    public static boolean convertToBoolean(Value value) {
        if (!(value instanceof Literal)) {
            return false;
        }
        return convertToBoolean((Literal) value);
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
        return convertToCalendarSilent((Literal) value);
    }

    /**
     * Returns the value of a literal as an XSDDateTime instance if possible, otherwise return null.
     * @param literal a literal representing date/time
     * @return the value of the literal as XSDDateTime or null
     */
    public static XMLGregorianCalendar convertToCalendarSilent(Literal literal) { // TODO
        try {
            return literal.calendarValue();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Returns the literal comparison type for objects of the given quads.
     * If quads contain a quad with a literal object, then returns the type of this  literal, otherwise
     * the comparison cannot be determined and returns null.
     * @param statements collection of (conflicting) statements
     * @return the best comparison type or null
     */
    public static EnumLiteralType getComparisonType(Collection<Statement> statements) {
        // Use a heuristic:
        // - if no literal object is present, return null
        // - if only one type of literals is present, return this type
        // - if more than one type is present, remember the first two types encountered
        //   - as soon as one of these two types has two or more votes more than the other, return it
        //   - otherwise return the type with more votes or the first encountered type on equality
        EnumLiteralType firstGuess = null;
        int firstGuessCount = 0;
        EnumLiteralType secondGuess = null;
        int secondGuessCount = 0;
        for (Statement statement : statements) {
            if (statement.getObject() instanceof Literal) {
                EnumLiteralType type = getLiteralType((Literal) statement.getObject());
                if (firstGuess == null || firstGuess == type) {
                    firstGuess = type;
                    firstGuessCount++;
                } else if (secondGuess == null || secondGuess == type) {
                    firstGuess = type;
                    firstGuessCount++;
                }
                int voteDifference = firstGuessCount - secondGuessCount;
                final int voteTreshold = 2;
                if (voteDifference >= voteTreshold || voteDifference <= -voteTreshold) {
                    break;
                }
            }
        }
        return firstGuessCount > secondGuessCount ? firstGuess : secondGuess;
    }

    /** Disable constructor for a utility class. */
    private ResolutionFunctionUtils() {
    }
}
