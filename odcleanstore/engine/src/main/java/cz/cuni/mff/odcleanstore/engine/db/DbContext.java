package cz.cuni.mff.odcleanstore.engine.db;

import java.sql.Connection;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

/**
 * Base class wrapping a database connection.
 * @author Petr Jerman
 */
public abstract class DbContext {
    
    private static final String ERROR_CLOSED_CONNECTION = "Attempt to closed connection";
    
    private VirtuosoConnectionWrapper connection = null;

    protected DbContext() {
        
    }
    
    protected void setConnection(JDBCConnectionCredentials connectionCredentials) throws ConnectionException {
        connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);
        connection.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL);
        connection.adjustTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
    }
    
    public void closeQuietly() {
        if (connection != null) {
            try {
                connection.rollback();
                connection.close();
            } catch (Throwable e) {
                // do nothing
            }
            connection = null;
        }
    }

    public void commit() throws ConnectionException, DbTransactionException {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new DbTransactionException("");
        }
    }
    
    public void rollback() throws ConnectionException, SQLException {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }
        connection.rollback();
    }
    
    protected WrappedResultSet select(String query) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }
        return connection.executeSelect(query);
    }
    
    protected WrappedResultSet select(String query, Object... objects) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }        
        return connection.executeSelect(query, objects);
    }
    
    protected int execute(String query) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }        
        return connection.execute(query);
    }
    
    protected int execute(String query, Object... objects) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }        
        return connection.execute(query, objects);
    }
    
    protected int executeNullsAlllowed(String query, Object... objects) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }        
        return connection.executeNullsAllowed(query, objects);
    }
    
    protected static void close(WrappedResultSet resultSet) {
        if (resultSet != null) {
            resultSet.closeQuietly();
        }
    }
}
