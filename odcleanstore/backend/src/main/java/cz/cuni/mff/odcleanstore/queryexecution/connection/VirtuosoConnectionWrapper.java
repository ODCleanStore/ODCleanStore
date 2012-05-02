package cz.cuni.mff.odcleanstore.queryexecution.connection;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.queryexecution.exceptions.QueryException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A wrapper for SQL {@link Connection} to a Virtuoso database.
 * Wraps {@link SQLException} an ODCleanStoreException and serves as a factory class
 * for {@link WrappedResultSet}.
 *
 * Non-static methods are not thread-safe.
 * @author Jan Michelfeit
 */
public final class VirtuosoConnectionWrapper {
    /**
     * Query timeout in seconds.
     * TODO: get from global configuration
     */
    public static final int QUERY_TIMEOUT = 30;

    /**
     * Create a new connection and return its wrapper.
     * Should be used only for connection to a Virtuoso instance.
     * @param sparqlEndpoint connection settings for the SPARQL endpoint
     * @return wrapper of the newly created connection
     * @throws ConnectionException database connection error
     */
    public static VirtuosoConnectionWrapper createConnection(SparqlEndpoint sparqlEndpoint) throws ConnectionException {
        try {
            Class.forName("virtuoso.jdbc3.Driver");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Couldn't load Virtuoso jdbc driver", e);
        }
        try {
            Connection connection = DriverManager.getConnection(
                    sparqlEndpoint.getUri(),
                    sparqlEndpoint.getUsername(),
                    sparqlEndpoint.getPassword());
            return new VirtuosoConnectionWrapper(connection);
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    /** Database connection. */
    private Connection connection;

    /**
     * Create a new instance.
     * @see #createConnection(SparqlEndpoint)
     * @param connection a connection to a Virtuoso database
     */
    private VirtuosoConnectionWrapper(Connection connection) {
        this.connection = connection;
    }

    /**
     * Executes an SQL/SPARQL SELECT query and returns a wrapper for the result.
     * @param query SQL/SPARQL query
     * @return the result of the query
     * @throws QueryException query error
     */
    public WrappedResultSet executeSelect(String query) throws QueryException {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(QUERY_TIMEOUT);
            statement.execute(query);
            return new WrappedResultSet(statement);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    /**
     * Executes a SPARQL ASK query and returns the result.
     * @param query SQL/SPARQL query
     * @return the result of the query
     * @throws QueryException query error
     */
    public boolean executeAsk(String query) throws QueryException {
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(QUERY_TIMEOUT);
            statement.execute(query);
            resultSet = statement.getResultSet();
            if (!resultSet.next()) {
                return false;
            }
            return (resultSet.getInt(1) == 1);
        } catch (SQLException e) {
            throw new QueryException(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    // Do nothing
                }
            }
        }
    }

    /**
     * Closes the wrapped connection.
     * @throws ConnectionException connection error
     */
    public void close() throws ConnectionException {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Close connection on finalize().
     */
    @Override
    protected void finalize() {
        try {
            close();
            super.finalize();
        } catch (Throwable e) {
            // do nothing
        }
    }
}
