/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedLiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LexicalLengthComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.ReverseOrderComparator;

/**
 * @author Jan Michelfeit
 */
public class ShortestResolution extends BestSelectedObjectResolutionBase {
    private  static final String FUNCTION_NAME = "SHORTEST";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private static final BestSelectedLiteralComparator COMPARATOR =
            new ReverseOrderComparator(new LexicalLengthComparator());
    
    public ShortestResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }

    @Override
    protected BestSelectedLiteralComparator getComparator(Model statements, CRContext crContext) {
        return COMPARATOR;
    }
}
