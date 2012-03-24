package cz.cuni.mff.odcleanstore.queryexecution;

import java.util.Date;

/**
 * Encapsulates constraints on user queries over the RDF database.
 * 
 * @todo replace Date by Calendar?
 * @author Jan Michelfeit
 */
public class QueryConstraintSpec {
    /**
     * Oldest time of accepted triples. Triples with stored time of the respective
     * named graph strictly older than this will be ignored.
     * Null means accept all triples.
     */
    private Date oldestTime = null;

    /**
     * Minimum error localization score of accepted named graphs.
     * Null means accept all graphs.
     */
    private Double minScore = null;

    /**
     * Create instance with no contraints.
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
     * time strictly older will be ignord. Null means no limit.
     * @param oldestTime oldest time of accepted named graphs or null
     */
    public void setOldestTime(Date oldestTime) {
        this.oldestTime = oldestTime;
    }

    /**
     * Returns the minimum score of accepted named graphs of null if there is no
     * limit.
     * @return minimum score ofÂ´accepted named graphs or null
     */
    public Double getMinScore() {
        return minScore;
    }

    /**
     * Sets the minimum score of accepted named graphs of null if there is no
     * limit.
     * @param minScore minimum score of accepted named graphs or null
     */
    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }
}
