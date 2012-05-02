package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.conflictresolution.CRQuad;
import cz.cuni.mff.odcleanstore.conflictresolution.NamedGraphMetadataMap;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * Executes the keyword search query.
 * Triples that contain the given keywords (separated by whitespace) in the object of the triple
 * of type literal are returned.
 *
 * This class is not thread-safe.
 *
 * @author Jan Michelfeit
 */
/*package*/class KeywordQueryExecutor extends QueryExecutorBase {
    private static final Logger LOG = LoggerFactory.getLogger(KeywordQueryExecutor.class);

    /**
     * Database connection.
     */
    private VirtuosoConnectionWrapper connection;

    /**
     * Creates a new instance of KeywordQueryExecutor.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     */
    public KeywordQueryExecutor(SparqlEndpoint sparqlEndpoint, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        super(sparqlEndpoint, constraints, aggregationSpec);
    }

    /**
     * Executes the keyword search query.
     *
     * @param keywords searched keywords (separated by whitespace)
     * @return query result holder
     * @throws ODCleanStoreException database error
     */
    public QueryResult findKeyword(String keywords) throws ODCleanStoreException {
        LOG.info("Keyword query for {}", keywords);
        long startTime = System.currentTimeMillis();
        // TODO: escaping

        try {
            return createResult(Collections.<CRQuad>emptyList(), new NamedGraphMetadataMap(),
                    System.currentTimeMillis() - startTime);
        } finally {
            closeConnection();
        }
    }

    /**
     * Returns a database connection.
     * The connection is shared within this instance until it is closed.
     * @return database connection
     * @throws ConnectionException database connection error
     */
    /*private VirtuosoConnectionWrapper getConnection() throws ConnectionException {
        if (connection == null) {
            connection = VirtuosoConnectionWrapper.createConnection(sparqlEndpoint);
        }
        return connection;
    }*/

    /**
     * Closes an opened database connection, if any.
     * @throws ConnectionException database connection error
     */
    private void closeConnection() throws ConnectionException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    /**
     * Creates an object holding the results of the query.
     * @param resultQuads result of the query as {@link CRQuad CRQuads}
     * @param metadata provenance metadata for resultQuads
     * @param executionTime query execution time in ms
     * @return query result holder
     */
    private QueryResult createResult(
            Collection<CRQuad> resultQuads,
            NamedGraphMetadataMap metadata,
            long executionTime) {

        LOG.debug("Query Execution: findKeyword() in {} ms", executionTime);
        // Format and return result
        QueryResult queryResult = new QueryResult(resultQuads, metadata, EnumQueryType.KEYWORD, constraints,
                aggregationSpec);
        queryResult.setExecutionTime(executionTime);
        return queryResult;
    }
}


