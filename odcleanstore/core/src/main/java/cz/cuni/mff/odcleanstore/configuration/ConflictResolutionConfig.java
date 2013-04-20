package cz.cuni.mff.odcleanstore.configuration;

/**
 * Conflict Resolution configuration values.
 * @author Jan Michelfeit
 */
public interface ConflictResolutionConfig {
    /**
     * Coefficient used quality computation formula; value N means that (N+1) sources
     * with score 1 that agree on the result will increase the result quality to 1.
     * @return agree coefficient
     */
    Double getAgreeCoeficient();
    
    /**
     * Named graph score used if none is given in the input.
     * @return Named graph score used if none is given in the input.
     */
    Double getScoreIfUnknown();

    /**
     * Weight of the named graph score in total source score.
     * @return Weight of the named graph score
     */
    Double getNamedGraphScoreWeight();

    /**
     * Weight of the publisher score in total source score.
     * @return Weight of the publisher score
     */
    Double getPublisherScoreWeight();

    /**
     * Minimum difference between two dates to consider them completely different  in seconds.
     * @return number of seconds 
     */
    Long getMaxDateDifference();
}
