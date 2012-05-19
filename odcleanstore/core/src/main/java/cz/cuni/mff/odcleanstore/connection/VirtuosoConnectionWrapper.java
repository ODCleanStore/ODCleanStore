package cz.cuni.mff.odcleanstore.connection;

import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
    public static VirtuosoConnectionWrapper createConnection(ConnectionCredentials sparqlEndpoint) throws ConnectionException {
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
     * @see #createConnection(ConnectionCredentials)
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
     * Executes an SQL/SPARQL SELECT query and returns a wrapper for the result.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @return the result of the query
     * @throws QueryException query error
     */
    public WrappedResultSet executeSelect(String query, Object... objects) throws QueryException {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < objects.length; ++i) {
                statement.setObject(i + 1, objects[i]);
            }

            statement.setQueryTimeout(QUERY_TIMEOUT);
            statement.execute();
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
     * Executes a general SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @throws QueryException query error
     */
    public void execute(String query) throws QueryException {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(QUERY_TIMEOUT);
            statement.execute(query);
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    /**
     * Executes a general SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @throws QueryException query error
     */
    public void execute(String query, Object... objects) throws QueryException {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < objects.length; ++i) {
                statement.setObject(i + 1, objects[i]);
            }

            statement.setQueryTimeout(QUERY_TIMEOUT);
            statement.execute();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    /**
     * Commit changes to the database.
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *         distributed transaction, if this method is called on a closed connection or this Connection object is in
     *         auto-commit mode
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Revert changes to the last commit.
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *         distributed transaction, if this method is called on a closed conection or this Connection object is in
     *         auto-commit mode
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Adjust transaction logging level and auto commit.
     * @param logLevel Virtuoso transaction logging level
     * @param autoCommit enable/disable auto commit
     * @throws ConnectionException database error
     */
    public void adjustTransactionLevel(EnumLogLevel logLevel, boolean autoCommit) throws ConnectionException {
        try {
            CallableStatement statement = connection.prepareCall(String.format("log_enable(%d)", logLevel.getBits()));
            statement.execute();
            connection.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            throw new ConnectionException(e);
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