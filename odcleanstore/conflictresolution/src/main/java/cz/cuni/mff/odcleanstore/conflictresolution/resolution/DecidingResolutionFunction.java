/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;



/**
 * @author Jan Michelfeit
 */
public abstract class DecidingResolutionFunction extends ResolutionFunctionBase {
    protected DecidingResolutionFunction(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator);
    }
}
