package cz.cuni.mff.odcleanstore.queryexecution;

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
 * The base class of query executors - classes that handle each type of query over the clean
 * database.
 *
 * Each query executor loads triples relevant for the query from the clean database, applies
 * conflict resolution to it and converts the result to plain RDF quads.
 *
 * @author Jan Michelfeit
 */
/*package*/abstract class QueryExecutorBase {

    // TODO: remove
    protected static final String NG_PREFIX_FILTER = "http://odcs.mff.cuni.cz/namedGraph/qe-test/";
    protected static final long DEFAULT_LIMIT = 200;
    protected static final String RESULT_GRAPH_PREFIX = "http://odcs.mff.cuni.cz/results/";

    protected static final String[] LABEL_PROPERTIES = new String[] { RDFS.label };
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
    protected final SparqlEndpoint sparqlEndpoint;

    private Connection connection;

    /**
     * Creates a new instance of QueryExecutorBase.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint that will be queried
     */
    protected QueryExecutorBase(SparqlEndpoint sparqlEndpoint) {
        this.sparqlEndpoint = sparqlEndpoint;
    }

    private Connection getConnection() throws ODCleanStoreException {
        if (connection == null) {
            connection = createConnection();
        }
        return connection;
    }

    private Connection createConnection() throws ODCleanStoreException {
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

    protected WrappedResultSet executeQuery(String query) throws ODCleanStoreException {
        try {
            Statement statement = getConnection().createStatement();
            statement.execute(query);
            return new WrappedResultSet(statement);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }




}
