package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

/**
 * Comparator of quads by the lexical form of their object.
 * The object must be a literal
 */
public class StringLiteralComparator implements LiteralComparator {
    private static final StringLiteralComparator INSTANCE = new StringLiteralComparator();

    public static final StringLiteralComparator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean accept(Value object, CRContext crContext) {
        return object instanceof Literal;
    }

    @Override
    public int compare(Value object1, Value object2) {
        return ODCSUtils.nullProofCompare(object1.stringValue(), object2.stringValue());
    }
}
