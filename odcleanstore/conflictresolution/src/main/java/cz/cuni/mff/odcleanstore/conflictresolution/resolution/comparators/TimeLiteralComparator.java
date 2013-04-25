package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import javax.xml.datatype.XMLGregorianCalendar;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.XMLGregorianCalendarComparator;

/**
 * Comparator of quads having a time literal as the object.
 * The object must be a literal of type EnumLiteralType.TIME.
 */
public class TimeLiteralComparator implements LiteralComparator {
    private static final TimeLiteralComparator INSTANCE = new TimeLiteralComparator();

    public static final TimeLiteralComparator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean accept(Value object, CRContext crContext) {
        if (!(object instanceof Literal)) {
            return false;
        }
        return ResolutionFunctionUtils.getLiteralType((Literal) object) == EnumLiteralType.TIME;
    }

    @Override
    public int compare(Value object1, Value object2) {
        XMLGregorianCalendar value1 = ResolutionFunctionUtils.convertToCalendarSilent(object1);
        XMLGregorianCalendar value2 = ResolutionFunctionUtils.convertToCalendarSilent(object2);
        return XMLGregorianCalendarComparator.getInstance().compare(value1, value2);
    }
}