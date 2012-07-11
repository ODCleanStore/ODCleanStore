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
public class BasicQueryResult extends QueryResultBase {
    /** The result of the query as CRQuads. */
    private Collection<CRQuad> resultQuads;

    /** Provenance metadata for {@link #resultQuads}. */
    private NamedGraphMetadataMap metadata;

    /** Constraints on triples returned in the result. */
    private QueryConstraintSpec queryConstraints;

    /** Aggregation settings for conflict resolution. */
    private AggregationSpec aggregationSpec;

    /**
     * Initializes a new instance.
     * @param resultQuads result of the query as {@link CRQuad CRQuads}
     * @param metadata provenance metadata for resultQuads
     * @param query the query string
     * @param queryType type of the query
     * @param queryConstraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings used during conflict resolution
     */
    public BasicQueryResult(
            Collection<CRQuad> resultQuads,
            NamedGraphMetadataMap metadata,
            String query,
            EnumQueryType queryType,
            QueryConstraintSpec queryConstraints,
            AggregationSpec aggregationSpec) {

        super(query, queryType);
        this.resultQuads = resultQuads;
        this.metadata = metadata;
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

}
