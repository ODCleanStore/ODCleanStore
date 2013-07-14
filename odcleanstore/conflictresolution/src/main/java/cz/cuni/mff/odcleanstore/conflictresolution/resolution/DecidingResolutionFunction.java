/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;

/**
 * Base class for deciding conflict resolution functions (i.e. those choosing one or more values from their input).
 * @author Jan Michelfeit
 */
public abstract class DecidingResolutionFunction extends ResolutionFunctionBase {
    /**
     * Creates a new instance.
     * @param fQualityCalculator calculator of F-quality to be used for estimation of 
     *      produced {@link cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement result quads} 
     *      (see {@link cz.cuni.mff.odcleanstore.conflictresolution.quality.FQualityCalculator}) 
     */
    protected DecidingResolutionFunction(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }
}
