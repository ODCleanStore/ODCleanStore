package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.ConflictResolutionPolicy;
import cz.cuni.mff.odcleanstore.conflictresolution.ResolvedStatement;

import org.openrdf.model.Model;

import java.util.Collection;

/**
 * Query result holder.
 * Provides access to all important information about query result such as the result quads, metadata or query settings.
 * @author Jan Michelfeit
 */
public class BasicQueryResult extends QueryResultBase {
    /** The result of the query as {@link ResolvedStatement ResolvedStatements}. */
    private Collection<ResolvedStatement> resultQuads;

    /** Provenance metadata for {@link #resultQuads}. */
    private Model metadata;

    /** Constraints on triples returned in the result. */
    private QueryConstraintSpec queryConstraints;

    /** Aggregation settings for conflict resolution. */
    private ConflictResolutionPolicy conflictResolutionPolicy;

    /**
     * Initializes a new instance.
     * @param resultQuads result of the query as {@link CRQuad CRQuads}
     * @param metadata provenance metadata for resultQuads
     * @param query the query string
     * @param queryType type of the query
     * @param queryConstraints constraints on triples returned in the result
     * @param conflictResolutionPolicy conflict resolution policy
     */
    public BasicQueryResult(
            Collection<ResolvedStatement> resultQuads,
            Model metadata,
            String query,
            EnumQueryType queryType,
            QueryConstraintSpec queryConstraints,
            ConflictResolutionPolicy conflictResolutionPolicy) {

        super(query, queryType);
        this.resultQuads = resultQuads;
        this.metadata = metadata;
        this.queryConstraints = queryConstraints;
        this.conflictResolutionPolicy = conflictResolutionPolicy;
    }

    /**
     * Returns the result of the query as {@link ResolvedStatement ResolvedStatements}.
     * @return quads forming the result of the query
     */
    public Collection<ResolvedStatement> getResultQuads() {
        return resultQuads;
    }

    /**
     * Returns provenance metadata for quads returned by {@link #getResultQuads()}.
     * @return  metadata
     */
    public Model getMetadata() {
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
     * Returns conflict resolution strategies.
     * @return conflict resolution strategies
     */
    public ConflictResolutionPolicy getConflictResolutionPolicy() {
        return conflictResolutionPolicy;
    }

}
