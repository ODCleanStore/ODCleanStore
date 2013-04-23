/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public abstract class MediatingResolutionFunction extends ResolutionFunctionBase {
    protected MediatingResolutionFunction(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator);
    }
}
