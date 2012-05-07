package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.shared.EnumLiteralType;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * Comparator of quads having a numeric literal as the object.
 * The object must be a literal of type EnumLiteralType.NUMERIC.
 */
public class NumericLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Quad quad) {
        Node object = quad.getObject();
        return object.isLiteral() && AggregationUtils.getLiteralType(object) == EnumLiteralType.NUMERIC;
    }

    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        double value1 = AggregationUtils.convertToDoubleSilent(quad1.getObject().getLiteral());
        double value2 = AggregationUtils.convertToDoubleSilent(quad2.getObject().getLiteral());
        if (value1 == value2) {
            return 0;
        } else if (value1 > value2) {
            return 1;
        } else {
            return -1;
        }
    }
}