/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import org.openrdf.model.Statement;
import org.openrdf.model.Value;

import cz.cuni.mff.odcleanstore.conflictresolution.CRContext;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;

/**
 * Implementation of {@link BestSelectedResolutionBase} which compares objects of quads.
 * @author Jan Michelfeit
 */
public abstract class BestSelectedObjectResolutionBase extends BestSelectedResolutionBase<Value> {
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    protected BestSelectedObjectResolutionBase(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }
    
    @Override
    protected Value getComparedValue(Statement statement, CRContext crContext) {
        return statement.getObject();
    }
}
