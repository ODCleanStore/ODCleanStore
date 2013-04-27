/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedLiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparatorFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * @author Jan Michelfeit
 */
public class MaxResolution extends BestSelectedObjectResolutionBase {
    private  static final String FUNCTION_NAME = "MAX";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    public MaxResolution(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    protected BestSelectedLiteralComparator getComparator(Model statements, CRContext crContext) {
        EnumLiteralType comparisonType = ResolutionFunctionUtils.getComparisonType(statements);
        if (comparisonType == null) {
            comparisonType = EnumLiteralType.OTHER;
        }
        return LiteralComparatorFactory.getComparator(comparisonType);
    }
}
