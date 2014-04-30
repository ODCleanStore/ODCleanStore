package cz.cuni.mff.odcleanstore.connection;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.CallableStatement;
import java.sql.Connection;
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
public final class JenaVirtuosoConnectionWrapper implements VirtuosoConnectionWrapper {
    private static final Logger LOG = LoggerFactory.getLogger(JenaVirtuosoConnectionWrapper.class);

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
    /*package*/ JenaVirtuosoConnectionWrapper(Connection connection) {
        this.connection = connection;
        if (ConfigLoader.isConfigLoaded()) {
            queryTimeout = ConfigLoader.getConfig().getBackendGroup().getQueryTimeout();
        }
    }

    @Override
    public void setQueryTimeout(int queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
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

    @Override
    public void adjustTransactionIsolationLevel(int level) throws ConnectionException {
        try {
            connection.setTransactionIsolation(level);
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
    public void close() throws ConnectionException {
        try {
            LOG.debug("VirtuosoConnectionWrapper: closing connection");
            connection.close();
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    @Override
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
