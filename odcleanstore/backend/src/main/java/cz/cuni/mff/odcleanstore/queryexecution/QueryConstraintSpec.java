package cz.cuni.mff.odcleanstore.queryexecution;

import java.util.Date;

/**
 * Encapsulates constraints on user queries over the RDF database.
 *
 * @author Jan Michelfeit
 */
public class QueryConstraintSpec {
    /**
     * Oldest time of accepted triples. Triples with stored time of the respective
     * named graph strictly older than this will be ignored.
     * Null means accept all triples (even those not having a stored time).
     */
    private Date oldestTime = null;

    /**
     * Minimum error localization score of accepted named graphs.
     * Null means accept all graphs (even those not having a score).
     */
    private Double minScore = null;

    /**
     * Create instance with no constraints.
     */
    public QueryConstraintSpec() {
    }

    /**
     * Create instance with the specified constraints.
     * @param oldestTime oldest time of accepted named graphs
     * @param minScore minimum score of accepted named graphs
     */
    public QueryConstraintSpec(Date oldestTime, Double minScore) {
        this.oldestTime = oldestTime;
        this.minScore = minScore;
    }

    /**
     * Returns the oldest time of accepted named graphs of null if there is no
     * limit.
     * @return oldest time of accepted named graphs or null
     */
    public Date getOldestTime() {
        return oldestTime;
    }

    /**
     * Sets the oldest time of accepted named graphs. Named graphs with stored
     * time strictly older will be ignored. Null means no limit.
     * @param oldestTime oldest time of accepted named graphs or null
     */
    public void setOldestTime(Date oldestTime) {
        this.oldestTime = oldestTime;
    }

    /**
     * Returns the minimum score of accepted named graphs of null if there is no
     * limit.
     * @return minimum score of accepted named graphs or null
     */
    public Double getMinScore() {
        return minScore;
    }

    /**
     * Sets the minimum score of accepted named graphs of null if there is no
     * limit.
     * @param minScore minimum score of accepted named graphs or null; the value must be in [0, 1]
     */
    public void setMinScore(Double minScore) {
        if (minScore != null && (minScore < 0 || 1 < minScore)) {
            throw new IllegalArgumentException("Named graph score must be from range [0,1] or null");
        }
        this.minScore = minScore;
    }
}
