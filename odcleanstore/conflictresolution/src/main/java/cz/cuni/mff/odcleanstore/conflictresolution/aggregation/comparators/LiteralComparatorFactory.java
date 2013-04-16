package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;

/**
 * A factory for AggregationComparators comparing quads by the literal in place of the object.
 * Implemented as flyweight - instances of comparators are cached (and created on demand).
 * @author Jan Michelfeit
 */
public final class LiteralComparatorFactory {
    private static final Logger LOG = LoggerFactory.getLogger(LiteralComparatorFactory.class);

    private static Map<EnumLiteralType, AggregationComparator> comparators =
            new HashMap<EnumLiteralType, AggregationComparator>(EnumLiteralType.values().length);
    private static Map<EnumLiteralType, AggregationComparator> reverseComparators =
            new HashMap<EnumLiteralType, AggregationComparator>(EnumLiteralType.values().length);

    /**
     * Creates a new instance of comparator best suitable for the given literal type.
     * @param literalType a literal type
     * @return a new instance of comparator
     */
    private static AggregationComparator createComparator(EnumLiteralType literalType) {
        switch (literalType) {
        case BOOLEAN:
            return new BooleanLiteralComparator();
        case DATE:
            return new DateLiteralComparator();
        case TIME:
            return new TimeLiteralComparator();
        case NUMERIC:
            return new NumericLiteralComparator();
        case OTHER:
        case STRING:
            return new StringLiteralComparator();
        default:
            LOG.error("Unhandled type of literal {} in {}.",
                    literalType.name(), LiteralComparatorFactory.class.getSimpleName());
            throw new RuntimeException("Unhandled type of literal");

        }
    }

    /**
     * Returns a new instance of comparator best suitable for the given literal type.
     * @param literalType a literal type
     * @return a comparator instance
     */
    public static AggregationComparator getComparator(EnumLiteralType literalType) {
        AggregationComparator comparator = comparators.get(literalType);
        if (comparator == null) {
            comparator = createComparator(literalType);
            comparators.put(literalType, comparator);
        }
        return comparator;
    }

    /**
     * Returns a new instance of comparator best suitable for the given literal type which sorts in the reverse order.
     * @param literalType a literal type
     * @return an instance of comparator sorting in the reverse order
     */
    public static AggregationComparator getReverseComparator(EnumLiteralType literalType) {
        AggregationComparator comparator = reverseComparators.get(literalType);
        if (comparator == null) {
            comparator = new ReverseAggregationComparator(createComparator(literalType));
            reverseComparators.put(literalType, comparator);
        }
        return comparator;
    }

    /** Hide constructor of a utility class. */
    private LiteralComparatorFactory() {
    }
}
