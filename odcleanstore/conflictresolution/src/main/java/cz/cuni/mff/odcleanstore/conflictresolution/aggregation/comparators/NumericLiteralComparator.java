package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;

/**
 * Comparator of quads having a numeric literal as the object.
 * The object must be a literal of type EnumLiteralType.NUMERIC.
 */
public class NumericLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Statement quad) {
        Value object = quad.getObject();
        return object instanceof Literal && AggregationUtils.getLiteralType(object) == EnumLiteralType.NUMERIC;
    }

    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        double value1 = AggregationUtils.convertToDoubleSilent(quad1.getObject());
        double value2 = AggregationUtils.convertToDoubleSilent(quad2.getObject());
        return Double.compare(value1, value2);
    }
}