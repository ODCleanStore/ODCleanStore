package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

import java.net.URISyntaxException;

/**
 * Access point of the Query Execution component.
 * Provides access to methods provided by each QueryExecutor.
 *
 * The purpose of Query Execution is to load triples relevant for the given query from the clean
 * database, apply conflict resolution to it, converts the result (collection of CRQuads)
 * to plain quads and return the result.
 *
 * Methods of this class are thread-safe.
 *
 * TODO: merge with default AggregationSpec
 *
 * @author Jan Michelfeit
 */
public class QueryExecution {
    /** Connection settings for the SPARQL endpoint that will be queried. */
    private final SparqlEndpoint sparqlEndpoint;

    /**
     * Creates a new instance of QueryExecution.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     */
    public QueryExecution(SparqlEndpoint sparqlEndpoint) {
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
     * @throws ODCleanStoreException exception
     *
     * @todo
     */
    public QueryResult findKeyword(String keywords, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) throws ODCleanStoreException {
        return new KeywordQueryExecutor(sparqlEndpoint, constraints, aggregationSpec).findKeyword(keywords);
    }

    /**
     * URI search query.
     * Triples that contain the given URI as their subject or object are returned.
     *
     * @param uri searched URI
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     * @return result of the query as RDF quads
     * @throws ODCleanStoreException exception
     * @throws URISyntaxException thrown when uri is not a valid URI
     */
    public QueryResult findURI(String uri, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) throws ODCleanStoreException, URISyntaxException {
        return new UriQueryExecutor(sparqlEndpoint, constraints, aggregationSpec).findURI(uri);
    }
}
