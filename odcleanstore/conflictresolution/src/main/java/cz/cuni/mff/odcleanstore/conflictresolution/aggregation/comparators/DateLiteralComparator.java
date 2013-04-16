package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import java.util.Calendar;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

/**
 * Comparator of quads having a date literal as the object.
 * The object must be a literal of type EnumLiteralType.DATE.
 */
public class DateLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Statement quad) {
        Value object = quad.getObject();
        return object instanceof Literal && AggregationUtils.getLiteralType(object) == EnumLiteralType.DATE;
    }

    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        Calendar value1 = AggregationUtils.convertToCalendarSilent(quad1.getObject()).toGregorianCalendar();
        Calendar value2 = AggregationUtils.convertToCalendarSilent(quad2.getObject()).toGregorianCalendar();
        return ODCSUtils.nullProofCompare(value1, value2);
    }
}