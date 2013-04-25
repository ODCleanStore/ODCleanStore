/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LexicalLengthComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.ReverseOrderComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparator;

/**
 * @author Jan Michelfeit
 */
public class ShortestResolution extends BestSelectedResolutionBase {
    private static final LiteralComparator COMPARATOR =
            new ReverseOrderComparator(new LexicalLengthComparator());
    
    public ShortestResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    protected LiteralComparator getComparator(Model statements, CRContext crContext) {
        return COMPARATOR;
    }
}
