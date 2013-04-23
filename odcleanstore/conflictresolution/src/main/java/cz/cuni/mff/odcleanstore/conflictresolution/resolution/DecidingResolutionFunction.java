/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;



/**
 * @author Jan Michelfeit
 */
public abstract class DecidingResolutionFunction extends ResolutionFunctionBase {
    protected DecidingResolutionFunction(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }
}
