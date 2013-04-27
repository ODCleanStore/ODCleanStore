/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;



/**
 * @author Jan Michelfeit
 */
public abstract class DecidingResolutionFunction extends ResolutionFunctionBase {
    protected DecidingResolutionFunction(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }
}
