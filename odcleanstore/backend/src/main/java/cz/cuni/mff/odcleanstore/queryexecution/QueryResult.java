package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;

import java.util.Collection;

/**
 * Query result holder.
 * Provides access to all important information about query result such as the result quads, metadata or query settings.
 * @author Jan Michelfeit
 */
public class QueryResult {
    /** The result of the query as CRQuads. */
    private Collection<CRQuad> resultQuads;

    /** Provenance metadata for {@link #resultQuads}. */
    private NamedGraphMetadataMap metadata;

    /** Type of the query. */
    private EnumQueryType queryType;

    /** Constraints on triples returned in the result. */
    private QueryConstraintSpec queryConstraints;

    /** Aggregation settings for conflict resolution. */
    private AggregationSpec aggregationSpec;

    /** Query execution time in ms. */
    private Long executionTime;

    /**
     * Initializes a new instance.
     * @param resultQuads result of the query as {@link CRQuad CRQuads}
     * @param metadata provenance metadata for resultQuads
     * @param queryType type of the query
     * @param queryConstraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings used during conflict resolution
     */
    public QueryResult(
            Collection<CRQuad> resultQuads,
            NamedGraphMetadataMap metadata,
            EnumQueryType queryType,
            QueryConstraintSpec queryConstraints,
            AggregationSpec aggregationSpec) {

        this.resultQuads = resultQuads;
        this.metadata = metadata;
        this.queryType = queryType;
        this.queryConstraints = queryConstraints;
        this.aggregationSpec = aggregationSpec;
    }

    /**
     * Returns the result of the query as {@link CRQuad CRQuads}.
     * @return quads forming the result of the query
     */
    public Collection<CRQuad> getResultQuads() {
        return resultQuads;
    }

    /**
     * Returns provenance metadata for quads returned by {@link #getResultQuads()}.
     * @return  metadata
     */
    public NamedGraphMetadataMap getMetadata() {
        return metadata;
    }

    /**
     * Returns type of the query.
     * @return type of the query
     */
    public EnumQueryType getQueryType() {
        return queryType;
    }

    /**
     * Returns constraints on triples returned in the result.
     * @return query constraints
     */
    public QueryConstraintSpec getQueryConstraints() {
        return queryConstraints;
    }

    /**
     * Returns aggregation settings used in conflict resolution.
     * @return aggregation settings
     */
    public AggregationSpec getAggregationSpec() {
        return aggregationSpec;
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
