package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.core.ODCSUtils;

/**
 * Comparator of RDF nodes by the lexical form of their object.
 * @author Jan Michelfeit
 */
public class StringLiteralComparator implements BestSelectedLiteralComparator {
    private static final StringLiteralComparator INSTANCE = new StringLiteralComparator();

    /**
     * Returns the shared default instance of this class.
     * @return instance of this class
     */
    public static final StringLiteralComparator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean accept(Value object, CRContext crContext) {
        return object instanceof Literal;
    }

    @Override
    public int compare(Value object1, Value object2, CRContext crContext) {
        return ODCSUtils.nullProofCompare(object1.stringValue(), object2.stringValue());
    }
}
