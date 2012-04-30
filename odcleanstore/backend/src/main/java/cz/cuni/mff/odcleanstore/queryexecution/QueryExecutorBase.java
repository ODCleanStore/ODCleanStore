package cz.cuni.mff.odcleanstore.queryexecution;

import cz.cuni.mff.odcleanstore.conflictresolution.AggregationSpec;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.ODCleanStoreException;
import cz.cuni.mff.odcleanstore.vocabulary.RDFS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The base class of query executors.
 *
 * Each query executor loads triples relevant for the query and metadata from the clean database, applies
 * conflict resolution to it and returns a holder of thr result quads and metadata.
 *
 * @author Jan Michelfeit
 */
/*package*/abstract class QueryExecutorBase {
    // TODO: remove
    protected static final String GRAPH_PREFIX_FILTER = "http://odcs.mff.cuni.cz/namedGraph/qe-test/";
    /**
     * Maximum number of triples returned by each database query (the overall result size may be larger).
     * TODO: get from global configuration.
     */
    protected static final long MAX_LIMIT = 200;

    /** Prefix named graphs where the resulting triples are placed. TODO: get from global configuration. */
    protected static final String RESULT_GRAPH_PREFIX = "http://odcs.mff.cuni.cz/results/";

    /** Properties designating a human-readable label. */
    protected static final String[] LABEL_PROPERTIES = new String[] { RDFS.label };

    /** List of {@link #LABEL_PROPERTIES} formatted to a string for use in a SPARQL query. */
    protected static final String LABEL_PROPERTIES_LIST;

    static {
        assert (LABEL_PROPERTIES.length > 0);
        StringBuilder sb = new StringBuilder();
        for (String property : LABEL_PROPERTIES) {
            sb.append('<');
            sb.append(property);
            sb.append(">, ");
        }
        LABEL_PROPERTIES_LIST = sb.substring(0, sb.length() - 2);
    }

    /** Connection settings for the SPARQL endpoint that will be queried. */
    private final SparqlEndpoint sparqlEndpoint;

    /** Constraints on triples returned in the result. */
    protected final QueryConstraintSpec constraints;

    /** Aggregation settings for conflict resolution. */
    protected final AggregationSpec aggregationSpec;

    /**
     * Creates a new instance of QueryExecutorBase.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     * @param constraints constraints on triples returned in the result
     * @param aggregationSpec aggregation settings for conflict resolution
     */
    protected QueryExecutorBase(SparqlEndpoint sparqlEndpoint, QueryConstraintSpec constraints,
            AggregationSpec aggregationSpec) {
        this.sparqlEndpoint = sparqlEndpoint;
        this.constraints = constraints;
        this.aggregationSpec = aggregationSpec;

    }

    /**
     * Create a new database connection.
     * @return a new database connection
     * @throws ODCleanStoreException the connection cannot be created.
     */
    protected Connection createConnection() throws ConnectionException {
        try {
            Class.forName("virtuoso.jdbc3.Driver");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Couldn't load Virtuoso jdbc driver", e);
        }
        try {
            return DriverManager.getConnection(
                    sparqlEndpoint.getUri(),
                    sparqlEndpoint.getUsername(),
                    sparqlEndpoint.getPassword());
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Executes an SQL query and returns a wrapper for the result.
     * @param connection connection used to execute the query
     * @param query SQL/SPARQL query
     * @return the result of the query
     * @throws ODCleanStoreException query error
     */
    protected static WrappedResultSet executeQuery(Connection connection, String query) throws QueryException {
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
            return new WrappedResultSet(statement);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
}
