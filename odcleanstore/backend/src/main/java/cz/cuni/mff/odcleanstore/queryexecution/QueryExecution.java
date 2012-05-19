package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;

/**
 * Access point (facade) of the Query Execution component.
 * Provides access to methods provided by each QueryExecutor.
 *
 * The purpose of Query Execution is to load triples relevant for the given query from the clean
 * database, apply conflict resolution to it, converts the result (collection of CRQuads)
 * to plain quads and return the result.
 *
 * Methods of this class are thread-safe.
 *
 * @author Jan Michelfeit
 */
public class QueryExecution {
    /** Connection settings for the SPARQL endpoint that will be queried. */
    private final ConnectionCredentials sparqlEndpoint;

    /** Default aggregation settings for conflict resolution (loaded from database). */
    private AggregationSpec defaultConfiguration;

    /**
     * Creates a new instance of QueryExecution.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     */
    public QueryExecution(ConnectionCredentials sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    /**
     * Keyword search query.
     * Triples that contain the given keywords (separated by whitespace) in the object of the triple
     * of type literal are returned.
     *
     * @param keywords searched keywords (separated by whitespace)
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public QueryResult findKeyword(String keywords, QueryConstraintSpec constraints, AggregationSpec aggregationSpec)
            throws QueryExecutionException {

        KeywordQueryExecutor queryExecutor =
                new KeywordQueryExecutor(sparqlEndpoint, constraints, aggregationSpec, getDefaultConfiguration());
        return queryExecutor.findKeyword(keywords);
    }

    /**
     * URI search query.
     * Triples that contain the given URI as their subject or object are returned.
     *
     * @param uri searched URI
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     * @return result of the query as RDF quads
     * @throws QueryExecutionException exception
     */
    public QueryResult findURI(String uri, QueryConstraintSpec constraints, AggregationSpec aggregationSpec)
            throws QueryExecutionException {

        UriQueryExecutor queryExecutor =
                new UriQueryExecutor(sparqlEndpoint, constraints, aggregationSpec, getDefaultConfiguration());
        return queryExecutor.findURI(uri);
    }

    /**
     * Returns the default aggregation settings for conflict resolution.
     * @return aggregation settings
     * @throws QueryExecutionException invalid settings in the database or a database error
     */
    private AggregationSpec getDefaultConfiguration() throws QueryExecutionException {
        if (defaultConfiguration == null) {
            try {
                // This may get executed by multiple threads, but that doesn't matter
                defaultConfiguration = new QueryExecutionConfigLoader(sparqlEndpoint).getDefaultSettings();
            } catch (DatabaseException e) {
                throw new QueryExecutionException(EnumQueryError.DATABASE_ERROR, e);
            }
        }
        return defaultConfiguration;
    }
}
