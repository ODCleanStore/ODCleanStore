/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.confidence;


/**
 * A marking interface for confidence calculators intended for deciding resolution strategies.
 * Introduced in order to prevent confusion with mediating confidence calculators.
 * @author Jan Michelfeit
 */
public interface DecidingConfidenceCalculator extends ConfidenceCalculator {
}
