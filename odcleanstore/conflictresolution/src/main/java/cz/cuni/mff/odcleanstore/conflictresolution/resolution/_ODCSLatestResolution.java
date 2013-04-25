/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.LiteralComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators._ODCSInsertedAtComparator;

/**
 * @author Jan Michelfeit
 */
public class _ODCSLatestResolution extends BestSelectedResolutionBase {
    private static final LiteralComparator COMPARATOR = new _ODCSInsertedAtComparator();

    public _ODCSLatestResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    protected LiteralComparator getComparator(Model statements, CRContext crContext) {
        return COMPARATOR;
    }
}
