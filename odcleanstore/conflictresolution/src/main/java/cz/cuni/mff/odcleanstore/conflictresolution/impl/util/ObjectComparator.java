package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import java.util.Comparator;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;

/**
 * Comparator of statements by object.
 * The comparison is done by {@link ValueComparator}.
 * @author Jan Michelfeit
 */
public class ObjectComparator implements Comparator<Statement> {
    private static final Comparator<Value> COMPARATOR = new ValueComparator();
    private static Comparator<Statement> instance = new ObjectComparator();

    /**
     * Returns the shared default instance.
     * @return the shared default instance
     */
    public static Comparator<Statement> getInstance() {
        return instance;
    }

    @Override
    public int compare(Statement s1, Statement s2) {
        return COMPARATOR.compare(s1.getObject(), s2.getObject());
    }
}
