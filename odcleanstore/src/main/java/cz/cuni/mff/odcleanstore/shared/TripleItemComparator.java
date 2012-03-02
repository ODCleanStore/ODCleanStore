package cz.cuni.mff.odcleanstore.shared;

import cz.cuni.mff.odcleanstore.graph.BlankTripleItem;
import cz.cuni.mff.odcleanstore.graph.LiteralTripleItem;
import cz.cuni.mff.odcleanstore.graph.TripleItem;
import cz.cuni.mff.odcleanstore.graph.URITripleItem;

/**
 * Comparator of two TripleItems.
 * The comparison can be used to sort TripleItems, equal triples are guaranteed
 * to return 0, however the class gives no promises about hte exact order of
 * TripleItems.
 * 
 * @author Jan Michelfeit
 */
public final class TripleItemComparator {
    /**
     * Compares two {@link TripleItem TripleItems}.
     * The comparison can be used to sort TripleItems, equal triples are guaranteed
     * to return 0, but the class gives no promises about hte exact order of
     * TripleItems.
     * 
     * The current implementation (can be changed) is based on the respective
     * resource URI, literal value or blank node and the class of TripleItem.
     * 
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the second.
     * @todo is enough to compare toString?
     */
    public static int compare(TripleItem o1, TripleItem o2) {
        if (o1.equals(o2)) {
            return 0;
        } else if (o1 instanceof URITripleItem && o2 instanceof URITripleItem) {
            return o1.getURI().compareTo(o2.getURI());
        } else if (o1 instanceof LiteralTripleItem && o2 instanceof LiteralTripleItem) {
            String value1 = ((LiteralTripleItem) o1).getValue();
            String value2 = ((LiteralTripleItem) o2).getValue();
            return value1.compareTo(value2);
        } else if (o1 instanceof BlankTripleItem && o2 instanceof BlankTripleItem) {
            String id1 = ((BlankTripleItem) o1).getAnonId();
            String id2 = ((BlankTripleItem) o2).getAnonId();
            return id1.compareTo(id2);
        } else {
            // Sort by class somehow, not important how
            int classComparison = o1.getClass().getName().compareTo(o2.getClass().getName());
            assert classComparison != 0 : "Unexpected child class of TripleItem";
            return classComparison;
        }
    }

    /** Disable constructor for a utility class. */
    private TripleItemComparator() {
    }
}
