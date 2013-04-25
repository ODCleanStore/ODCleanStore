/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparatorFactory;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.EnumLiteralType;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.utils.ResolutionFunctionUtils;

/**
 * @author Jan Michelfeit
 */
public class MinResolution extends BestSelectedResolutionBase {
    public MinResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    protected LiteralComparator getComparator(Model statements, CRContext crContext) {
        EnumLiteralType comparisonType = ResolutionFunctionUtils.getComparisonType(statements);
        if (comparisonType == null) {
            comparisonType = EnumLiteralType.OTHER;
        }
        return LiteralComparatorFactory.getReverseComparator(comparisonType);
    }
}
