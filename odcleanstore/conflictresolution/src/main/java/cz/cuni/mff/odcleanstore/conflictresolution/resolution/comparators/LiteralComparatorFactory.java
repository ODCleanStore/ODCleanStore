package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;

/**
 * A factory for AggregationComparators comparing quads by the literal in place of the object.
 * Implemented as flyweight - instances of comparators are cached (and created on demand).
 * @author Jan Michelfeit
 */
public final class LiteralComparatorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(LiteralComparatorFactory.class);

    private static Map<EnumLiteralType, LiteralComparator> reverseComparators =
            new HashMap<EnumLiteralType, LiteralComparator>(EnumLiteralType.values().length);

    /**
     * Returns a new instance of comparator best suitable for the given literal type.
     * @param literalType a literal type
     * @return a comparator instance
     */
    public static LiteralComparator getComparator(EnumLiteralType literalType) {
        switch (literalType) {
        case BOOLEAN:
            return BooleanLiteralComparator.getInstance();
        case DATE_TIME:
            return DateTimeLiteralComparator.getInstance();
        case TIME:
            return TimeLiteralComparator.getInstance();
        case NUMERIC:
            return NumericLiteralComparator.getInstance();
        case OTHER:
        case STRING:
            return StringLiteralComparator.getInstance();
        default:
            LOG.error("Unhandled type of literal {} in {}.",
                    literalType.name(), LiteralComparatorFactory.class.getSimpleName());
            throw new RuntimeException("Unhandled type of literal");
        }
    }

    /**
     * Returns a new instance of comparator best suitable for the given literal type which sorts in the reverse order.
     * @param literalType a literal type
     * @return an instance of comparator sorting in the reverse order
     */
    public static LiteralComparator getReverseComparator(EnumLiteralType literalType) {
        LiteralComparator comparator = reverseComparators.get(literalType);
        if (comparator == null) {
            comparator = new ReverseOrderComparator(getComparator(literalType));
            reverseComparators.put(literalType, comparator);
        }
        return comparator;
    }

    /** Hide constructor of a utility class. */
    private LiteralComparatorFactory() {
    }
}
