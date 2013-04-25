package cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils;

import java.util.Comparator;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Compares two dates objects based on the time part only.
 * Implemented as singleton.
 * @author Jan Michelfeit
 */
public final class XMLGregorianCalendarComparator implements Comparator<XMLGregorianCalendar> {
    private static final XMLGregorianCalendarComparator INSTANCE = new XMLGregorianCalendarComparator();

    @Override
    public int compare(XMLGregorianCalendar o1, XMLGregorianCalendar o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return -1;
        } else if (o2 == null) {
            return 1;
        } else {
            return o1.toGregorianCalendar().compareTo(o2.toGregorianCalendar());
        }
    }

    /**
     * Singleton - hide constructor.
     */
    private XMLGregorianCalendarComparator() {
    }

    /**
     * Get the singleton instance.
     * @return the singleton instance.
     */
    public static XMLGregorianCalendarComparator getInstance() {
        return INSTANCE;
    }
}
