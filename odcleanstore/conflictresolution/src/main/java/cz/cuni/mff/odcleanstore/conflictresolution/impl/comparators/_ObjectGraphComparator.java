package cz.cuni.mff.odcleanstore.conflictresolution.impl.comparators;

import java.util.Comparator;

import org.openrdf.model.Statement;
import org.openrdf.model.util.LexicalValueComparator;

    
public class _ObjectGraphComparator implements Comparator<Statement> {
    private static final LexicalValueComparator COMPARATOR = new LexicalValueComparator();
    private static Comparator<Statement> instance = new _ObjectGraphComparator();
    public static Comparator<Statement> getInstance() {
        return instance;
    }
    
    @Override
    public int compare(Statement s1, Statement s2) {
        int comparison = COMPARATOR.compare(s1.getObject(), s2.getObject());
        if (comparison != 0) {
            return comparison;
        }
        
        return COMPARATOR.compare(s1.getContext(), s2.getContext());
    } 
}