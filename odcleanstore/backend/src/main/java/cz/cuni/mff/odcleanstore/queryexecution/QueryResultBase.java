package cz.cuni.mff.odcleanstore.queryexecution;


/**
 * Base class for query result holders.
 * @author Jan Michelfeit
 */
public class QueryResultBase {
    /** The query string. */
    protected String query;

    /** Type of the query. */
    protected EnumQueryType queryType;

    /** Query execution time in ms. */
    private Long executionTime;

    /**
     * Initializes a new instance.
     * @param query the query string
     * @param queryType type of the query
     */
    public QueryResultBase(String query, EnumQueryType queryType) {
        this.query = query;
        this.queryType = queryType;
    }

    /**
     * Returns the query string.
     * @return the query string
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns type of the query.
     * @return type of the query
     */
    public EnumQueryType getQueryType() {
        return queryType;
    }

    /**
     * Set query execution time in ms.
     * @param executionTime execution time in ms
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    /**
     * Returns query execution time in ms.
     * @return execution time in ms or null if unknown
     */
    public Long getExecutionTime() {
        return executionTime;
    }

}
