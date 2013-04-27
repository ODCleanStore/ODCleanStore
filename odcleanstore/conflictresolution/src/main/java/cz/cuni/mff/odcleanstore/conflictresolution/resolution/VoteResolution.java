/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DecidingConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.DummySourceConfidenceCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.confidence.SourceConfidenceCalculator;

/**
 * @author Jan Michelfeit
 */
public class VoteResolution extends WeightedVoteResolution {
    private  static final String FUNCTION_NAME = "VOTE";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private static final SourceConfidenceCalculator SOURCE_CONFIDENCE_CALCULATOR = new DummySourceConfidenceCalculator();

    public VoteResolution(DecidingConfidenceCalculator confidenceCalculator) {
        super(confidenceCalculator, SOURCE_CONFIDENCE_CALCULATOR);
    }
}
