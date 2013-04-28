/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.BestSelectedComparator;
import cz.cuni.mff.odcleanstore.conflictresolution.resolution.comparators.ODCSInsertedAtComparator;

/**
 * @author Jan Michelfeit
 */
public class ODCSLatestResolution extends BestSelectedResolutionBase<Resource> {
    private static final BestSelectedComparator<Resource> COMPARATOR = new ODCSInsertedAtComparator();

    public ODCSLatestResolution(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }

    @Override
    protected BestSelectedComparator<Resource> getComparator(Model statements, CRContext crContext) {
        return COMPARATOR;
    }

    @Override
    protected Resource getComparedValue(Statement statement, CRContext crContext) {
        return statement.getContext();
    }
}
