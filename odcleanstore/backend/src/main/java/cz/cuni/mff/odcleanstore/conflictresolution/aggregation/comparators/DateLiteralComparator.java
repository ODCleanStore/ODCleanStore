package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.shared.EnumLiteralType;
import cz.cuni.mff.odcleanstore.shared.Utils;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.Quad;

import java.util.Calendar;

/**
 * Comparator of quads having a date literal as the object.
 * The object must be a literal of type EnumLiteralType.DATE.
 */
public class DateLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Quad quad) {
        Node object = quad.getObject();
        return object.isLiteral() && AggregationUtils.getLiteralType(object) == EnumLiteralType.DATE;
    }

    @Override
    public int compare(Quad quad1, Quad quad2, NamedGraphMetadataMap metadata) {
        Calendar value1 = AggregationUtils.convertToCalendarSilent(quad1.getObject());
        Calendar value2 = AggregationUtils.convertToCalendarSilent(quad2.getObject());
        return Utils.nullProofCompare(value1, value2);
    }
}