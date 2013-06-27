/**
 * 
 */
package cz.cuni.mff.odcleanstore.conflictresolution.resolution;

import cz.cuni.mff.odcleanstore.conflictresolution.quality.DecidingFQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.DummySourceQualityCalculator;
import cz.cuni.mff.odcleanstore.conflictresolution.quality.SourceQualityCalculator;

/**
 * @author Jan Michelfeit
 */
public class VoteResolution extends WeightedVoteResolution {
    private  static final String FUNCTION_NAME = "VOTE";
    public static String getName() {
        return FUNCTION_NAME;
    }
    
    private static final SourceQualityCalculator SOURCE_QUALITY_CALCULATOR = new DummySourceQualityCalculator();

    public VoteResolution(DecidingFQualityCalculator fQualityCalculator) {
        super(fQualityCalculator, SOURCE_QUALITY_CALCULATOR);
    }
}
