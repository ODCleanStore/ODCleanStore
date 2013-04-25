package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * Comparator of quads having a boolean literal as the object.
 * The object must be a literal.
 */
public class BooleanLiteralComparator implements LiteralComparator {
    private static final BooleanLiteralComparator INSTANCE = new BooleanLiteralComparator();
    
    public static final BooleanLiteralComparator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean accept(Value object, CRContext crContext) {
        return object instanceof Literal;
    }

    @Override
    public int compare(Value object1, Value object2) {
        boolean value1 = ResolutionFunctionUtils.convertToBoolean(object1);
        boolean value2 = ResolutionFunctionUtils.convertToBoolean(object2);
        if (value1 == value2) {
            return 0;
        } else if (value1 && !value2) {
            return 1;
        } else {
            return -1;
        }
    }
}