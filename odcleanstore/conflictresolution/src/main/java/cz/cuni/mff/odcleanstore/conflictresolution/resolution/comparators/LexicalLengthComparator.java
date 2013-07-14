package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * Comparator of literals by the length of their lexical representation.
 * @author Jan Michelfeit
 */
public class LexicalLengthComparator implements BestSelectedLiteralComparator {
    private static final LexicalLengthComparator INSTANCE = new LexicalLengthComparator();

    /**
     * Returns the shared default instance of this class.
     * @return instance of this class
     */
    public static final LexicalLengthComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean accept(Value object, CRContext crContext) {
        return true;
    }

    @Override
    public int compare(Value object1, Value object2, CRContext crContext) {
        return object1.stringValue().length()
                - object2.stringValue().length();
    }
}