package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;

/**
 * Comparator of quads by the lexical length of their object.
 */
public class LexicalLengthComparator implements LiteralComparator {
    private static final LexicalLengthComparator INSTANCE = new LexicalLengthComparator();

    public static final LexicalLengthComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean accept(Value object, CRContext crContext) {
        return true;
    }

    @Override
    public int compare(Value object1, Value object2) {
        return object1.stringValue().length()
                - object2.stringValue().length();
    }
}