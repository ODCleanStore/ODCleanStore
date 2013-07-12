package cz.cuni.mff.odcleanstore.conflictresolution.impl.util;

import java.util.Comparator;

import org.openrdf.model.Statement;
import org.openrdf.model.util.LexicalValueComparator;

/**
 * Comparator of statements by object.
 * The comparison is done by {@link LexicalValueComparator}.
 * @author Jan Michelfeit
 */
public class ObjectComparator implements Comparator<Statement> {
    private static final LexicalValueComparator COMPARATOR = new LexicalValueComparator();
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
