/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.db;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class wrapping a database connection with transactions.
 * 
 * @author Jan Michelfeit
 */
public class DbContextTransactional extends DbContext {

    @Override
    protected void setConnection(JDBCConnectionCredentials connectionCredentials) throws ConnectionException {
        super.setConnection(connectionCredentials);
        VirtuosoConnectionWrapper connection = getConnection();
        if (connection != null) {
            connection.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL);
            connection.adjustTransactionIsolationLevel(Connection.TRANSACTION_SERIALIZABLE);
        }
    }
    
    /**
     * Close without causing exceptions.
     */
    @Override
    public void closeQuietly() {
        VirtuosoConnectionWrapper connection = getConnection();
        if (connection != null) {
            try {
                connection.rollback();
            } catch (Throwable e) {
                // do nothing
            }
            super.closeQuietly();
        }
    }
    
    /**
     * Commit changes.
     * 
     * @throws ConnectionException
     * @throws DbTransactionException - Exception for transactions abort
     */
    public void commit() throws ConnectionException, DbTransactionException {
        VirtuosoConnectionWrapper connection = getConnection();
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }
        try {
            connection.commit();
        } catch (SQLException e) {
            throw new DbTransactionException("");
        }
    }
    
    /**
     * Rollback changes.
     * 
     * @throws ConnectionException
     * @throws SQLException
     */
    public void rollback() throws ConnectionException, SQLException {
        VirtuosoConnectionWrapper connection = getConnection();
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }
        connection.rollback();
    }
}
