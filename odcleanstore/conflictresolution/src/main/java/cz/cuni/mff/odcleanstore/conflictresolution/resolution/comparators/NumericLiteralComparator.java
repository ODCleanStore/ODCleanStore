package cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators;

import org.openrdf.model.Literal;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * Comparator of numeric literals
 * (of type {@link EnumLiteralType#NUMERIC - see {@link ResolutionFunctionUtils#getLiteralType(Literal)}).
 * @author Jan Michelfeit
 */
public class NumericLiteralComparator implements BestSelectedLiteralComparator {
    private static final NumericLiteralComparator INSTANCE = new NumericLiteralComparator();

    /**
     * Returns the shared default instance of this class.
     * @return instance of this class
     */
    public static final NumericLiteralComparator getInstance() {
        return INSTANCE;
    }
    
    @Override
    public boolean accept(Value object, CRContext crContext) {
        if (!(object instanceof Literal)) {
            return false;
        }
        return ResolutionFunctionUtils.getLiteralType((Literal) object) == EnumLiteralType.NUMERIC;
    }

    @Override
    public int compare(Value object1, Value object2, CRContext crContext) {
        double value1 = ResolutionFunctionUtils.convertToDoubleSilent(object1);
        double value2 = ResolutionFunctionUtils.convertToDoubleSilent(object2);
        return Double.compare(value1, value2);
    }
}