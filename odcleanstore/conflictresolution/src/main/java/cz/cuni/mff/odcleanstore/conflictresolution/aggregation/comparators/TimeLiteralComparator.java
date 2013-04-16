package cz.cuni.mff.odcleanstore.conflictresolution.aggregation.comparators;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.AggregationUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.aggregation.utils.TimeComparator;

/**
 * Comparator of quads having a time literal as the object.
 * The object must be a literal of type EnumLiteralType.TIME.
 */
public class TimeLiteralComparator implements AggregationComparator {
    @Override
    public boolean isAggregable(Statement quad) {
        Value object = quad.getObject();
        return object instanceof Literal && AggregationUtils.getLiteralType(object) == EnumLiteralType.TIME;
    }

    @Override
    public int compare(Statement quad1, Statement quad2, NamedGraphMetadataMap metadata) {
        XMLGregorianCalendar value1 = AggregationUtils.convertToCalendarSilent(quad1.getObject());
        XMLGregorianCalendar value2 = AggregationUtils.convertToCalendarSilent(quad2.getObject());
        return TimeComparator.getInstance().nullProofCompare(value1.toGregorianCalendar(), value2.toGregorianCalendar());
    }
}