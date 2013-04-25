/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.ConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DummySourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public class VoteResolution extends WeightedVoteResolution {
    private static final SourceConfidenceCalculator SOURCE_CONFIDENCE_CALCULATOR = new DummySourceConfidenceCalculator();

    public VoteResolution(ConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator, SOURCE_CONFIDENCE_CALCULATOR);
    }
}
