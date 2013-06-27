/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedLiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LexicalLengthComparator;

/**
 * @author Jan Michelfeit
 */
public class LongestResolution extends BestSelectedObjectResolutionBase {
    private  static final String FUNCTION_NAME = "LONGEST";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private static final BestSelectedLiteralComparator COMPARATOR = new LexicalLengthComparator();

    public LongestResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    protected BestSelectedLiteralComparator getComparator(Model statements, CRContext crContext) {
        return COMPARATOR;
    }
}
