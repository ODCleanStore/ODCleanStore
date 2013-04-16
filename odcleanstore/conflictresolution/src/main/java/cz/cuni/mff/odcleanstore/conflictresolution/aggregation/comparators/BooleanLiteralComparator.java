package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;

/**
 * Comparator of quads having a boolean literal as the object.
 * The object must be a literal.
 */
public class BooleanLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Statement quad) {
        return quad.getObject() instanceof Literal;
    }

    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        boolean value1 = AggregationUtils.convertToBoolean((Literal) quad1.getObject());
        boolean value2 = AggregationUtils.convertToBoolean((Literal) quad2.getObject());
        if (value1 == value2) {
            return 0;
        } else if (value1 && !value2) {
            return 1;
        } else {
            return -1;
        }
    }
}