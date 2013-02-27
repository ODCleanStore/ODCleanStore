package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Comparator of quads having a boolean literal as the object.
 * The object must be a literal.
 */
public class BooleanLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Quad quad) {
        return quad.getObject().isLiteral();
    }

    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        boolean value1 = AggregationUtils.convertToBoolean(quad1.getObject().getLiteral());
        boolean value2 = AggregationUtils.convertToBoolean(quad2.getObject().getLiteral());
        if (value1 == value2) {
            return 0;
        } else if (value1 && !value2) {
            return 1;
        } else {
            return -1;
        }
    }
}