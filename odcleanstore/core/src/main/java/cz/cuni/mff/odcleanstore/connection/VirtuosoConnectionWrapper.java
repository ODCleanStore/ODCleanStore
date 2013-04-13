package cz.cuni.mff.odcleanstore.connection;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.shared.ODCSUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * A wrapper for SQL {@link Connection} to a Virtuoso database.
 * Wraps {@link SQLException} an ODCleanStoreException and serves as a factory class
 * for {@link WrappedResultSet}.
 * 
 * Non-static methods are not thread-safe.
 * @author Jan Michelfeit
 */
public final class VirtuosoConnectionWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(VirtuosoConnectionWrapper.class);

    /**
     * Create a new connection and return its wrapper.
     * Should be used only for connection to a Virtuoso instance.
     * @param connectionCredentials connection settings for the SPARQL endpoint
     * @return wrapper of the newly created connection
     * @throws ConnectionException database connection error
     */
    public static VirtuosoConnectionWrapper createConnection(JDBCConnectionCredentials connectionCredentials)
            throws ConnectionException {

        try {
            Class.forName(ODCSUtils.JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Couldn't load Virtuoso jdbc driver", e);
        }
        try {
            LOG.debug("VirtuosoConnectionWrapper: creating connection");
            Connection connection = DriverManager.getConnection(
                    connectionCredentials.getConnectionString(),
                    connectionCredentials.getUsername(),
                    connectionCredentials.getPassword());
            VirtuosoConnectionWrapper wrapper = new VirtuosoConnectionWrapper(connection);
            // disable log by default in order to prevent log size problems; transactions don't work much with SPARQL anyway
            wrapper.adjustTransactionLevel(EnumLogLevel.AUTOCOMMIT);
            return wrapper;
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    /** Database connection. */
    private final Connection connection;

    /** Last logging level set by {@link #adjustTransactionLevel(EnumLogLevel)}. */
    private EnumLogLevel lastLogLevel = null;

    /**
     * Query timeout in seconds.
     * Default value loaded from global configuration settings.
     */
    private int queryTimeout = 0;

    /**
     * Create a new instance.
     * @see #createConnection(JDBCConnectionCredentials)
     * @param connection a connection to a Virtuoso database
     */
    private VirtuosoConnectionWrapper(Connection connection) {
        this.connection = connection;
        if (ConfigLoader.isConfigLoaded()) {
            queryTimeout = ConfigLoader.getConfig().getBackendGroup().getQueryTimeout();
        }
    }

    /**
     * Sets query timeout for all queries executed through this wrapper.
     * @param queryTimeout the new query timeout limit in seconds; zero means there is no limit
     */
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
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
            statement.setQueryTimeout(queryTimeout);
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

            statement.setQueryTimeout(queryTimeout);
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
            statement.setQueryTimeout(queryTimeout);
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
     * @return update count
     * @throws QueryException query error
     */
    public int execute(String query) throws QueryException {
        try {
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(queryTimeout);
            statement.execute(query);
            int updatedCount = statement.getUpdateCount();
            return updatedCount < 0 ? 0 : updatedCount;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    /**
     * Executes a general SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @return updated row count
     * @throws QueryException query error
     */
    public int execute(String query, Object... objects) throws QueryException {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < objects.length; ++i) {
                statement.setObject(i + 1, objects[i]);
            }

            statement.setQueryTimeout(queryTimeout);
            statement.execute();
            int updatedCount = statement.getUpdateCount();
            return updatedCount < 0 ? 0 : updatedCount;
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }

    /**
     * Executes a general SQL/SPARQL query with nulls object allowed.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @return updated row count
     * @throws QueryException query error
     */
    public int executeNullsAllowed(String query, Object... objects) throws QueryException {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < objects.length; ++i) {
                if (objects[i] != null) {
                    statement.setObject(i + 1, objects[i]);
                } else {
                    statement.setNull(i + 1, java.sql.Types.NULL);
                }
            }

            statement.setQueryTimeout(queryTimeout);
            statement.execute();
            int updatedCount = statement.getUpdateCount();
            return updatedCount < 0 ? 0 : updatedCount;
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
     * @param logLevel Virtuoso transaction logging level and auto-commit settings
     * @return old log level or null if the log level is unknown
     * @throws ConnectionException database error
     */
    public EnumLogLevel adjustTransactionLevel(EnumLogLevel logLevel) throws ConnectionException {
        if (logLevel == null) {
            return lastLogLevel;
        }
        try {
            CallableStatement statement = connection.prepareCall(
                    String.format(Locale.ROOT, "log_enable(%d, 1)", logLevel.getBits()));
            statement.execute();

            connection.setAutoCommit(logLevel.getAutocommit());

            EnumLogLevel oldLogLevel = lastLogLevel;
            lastLogLevel = logLevel;
            return oldLogLevel;
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Adjust transaction isolation level.
     * @param level - one of the following Connection constants:<br />
     *        Connection.TRANSACTION_READ_UNCOMMITTED,<br />
     *        Connection.TRANSACTION_READ_COMMITTED, <br />
     *        Connection.TRANSACTION_REPEATABLE_READ, <br />
     *        or Connection.TRANSACTION_SERIALIZABLE.
     * @throws ConnectionException database error
     */
    public void adjustTransactionIsolationLevel(int level) throws ConnectionException {
        try {
            connection.setTransactionIsolation(level);
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
            LOG.debug("VirtuosoConnectionWrapper: closing connection");
            connection.close();
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    /**
     * Closes the wrapped connection without throwing an exception.
     */
    public void closeQuietly() {
        try {
            close();
        } catch (ConnectionException e) {
            // Do nothing
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
