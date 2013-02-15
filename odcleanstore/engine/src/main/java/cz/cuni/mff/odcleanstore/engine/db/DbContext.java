package cz.cuni.mff.odcleanstore.engine.db;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

/**
 * Base class wrapping a database connection.
 * 
 * @author Petr Jerman
 */
public abstract class DbContext {
    
    protected static final String ERROR_CLOSED_CONNECTION = "Attempt to closed connection";
    
    private VirtuosoConnectionWrapper connection = null;

    protected DbContext() {
        
    }
    
    /**
     * Set connections to database.
     * 
     * @param connectionCredentials credentials for connections to database
     * @throws ConnectionException
     */
    protected void setConnection(JDBCConnectionCredentials connectionCredentials) throws ConnectionException {
        connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);
    }
    
    protected VirtuosoConnectionWrapper getConnection() {
        return connection;
    }
    
    /**
     * Close without causing exceptions.
     */
    public void closeQuietly() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable e) {
                // do nothing
            }
            connection = null;
        }
    }

    /**
     * Select query.
     * @param query
     * @return WrappedResultSet
     * @throws ConnectionException
     * @throws QueryException
     */
    protected WrappedResultSet select(String query) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }
        return connection.executeSelect(query);
    }
    
    /**
     * Select query with parameters.
     * 
     * @param query 
     * @param objects
     * @return WrappedResultSet
     * @throws ConnectionException
     * @throws QueryException
     */
    protected WrappedResultSet select(String query, Object... objects) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }        
        return connection.executeSelect(query, objects);
    }
    
    /**
     * Execute general query.
     * 
     * @param query
     * @return number of rows processed
     * @throws ConnectionException
     * @throws QueryException
     */
    protected int execute(String query) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }        
        return connection.execute(query);
    }
    
    /**
     * Execute general query with parameters.
     * @param query
     * @param objects
     * @return number of rows processed
     * @throws ConnectionException
     * @throws QueryException
     */
    protected int execute(String query, Object... objects) throws ConnectionException, QueryException  {
        if (connection == null) {
            throw new ConnectionException(ERROR_CLOSED_CONNECTION);
        }        
        return connection.execute(query, objects);
    }
    
    /**
     * Execute general query with parameters, nulls parameters are allowed.
     * @param query
     * @param objects
     * @return number of rows processed
     * @throws ConnectionException
     * @throws QueryException
     */
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
