package cz.cuni.mff.odcleanstore.connection;

import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

import java.sql.SQLException;

/**
 * A wrapper for SQL {@link java.sql.Connection} to a Virtuoso database.
 * 
 * Non-static methods are not thread-safe.
 * @author Jan Michelfeit
 */
public interface VirtuosoConnectionWrapper {
    /**
     * Sets query timeout for all queries executed through this wrapper.
     * @param queryTimeout the new query timeout limit in seconds; zero means there is no limit
     */
    void setQueryTimeout(int queryTimeout);

    /**
     * Executes an SQL/SPARQL SELECT query and returns a wrapper for the result.
     * @param query SQL/SPARQL query
     * @return the result of the query
     * @throws QueryException query error
     */
    WrappedResultSet executeSelect(String query) throws QueryException;

    /**
     * Executes an SQL/SPARQL SELECT query and returns a wrapper for the result.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @return the result of the query
     * @throws QueryException query error
     */
    WrappedResultSet executeSelect(String query, Object... objects) throws QueryException;
    
    /**
     * Executes a SPARQL ASK query and returns the result.
     * @param query SQL/SPARQL query
     * @return the result of the query
     * @throws QueryException query error
     */
    boolean executeAsk(String query) throws QueryException;
    
    /**
     * Executes a general SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @return update count
     * @throws QueryException query error
     */
    int execute(String query) throws QueryException;
    
    /**
     * Executes a general SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @return updated row count
     * @throws QueryException query error
     */
    int execute(String query, Object... objects) throws QueryException;
    
    /**
     * Executes a general SQL/SPARQL query with nulls object allowed.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @return updated row count
     * @throws QueryException query error
     */
    int executeNullsAllowed(String query, Object... objects) throws QueryException;
    
    /**
     * Commit changes to the database.
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *         distributed transaction, if this method is called on a closed connection or this Connection object is in
     *         auto-commit mode
     */
    void commit() throws SQLException;
    
    /**
     * Revert changes to the last commit.
     * @throws SQLException if a database access error occurs, this method is called while participating in a
     *         distributed transaction, if this method is called on a closed conection or this Connection object is in
     *         auto-commit mode
     */
    void rollback() throws SQLException;
    
    /**
     * Adjust transaction logging level and auto commit.
     * @param logLevel Virtuoso transaction logging level and auto-commit settings
     * @return old log level or null if the log level is unknown
     * @throws ConnectionException database error
     */
    EnumLogLevel adjustTransactionLevel(EnumLogLevel logLevel) throws ConnectionException;
    
    /**
     * Adjust transaction isolation level.
     * @param level - one of the following Connection constants:<br />
     *        Connection.TRANSACTION_READ_UNCOMMITTED,<br />
     *        Connection.TRANSACTION_READ_COMMITTED, <br />
     *        Connection.TRANSACTION_REPEATABLE_READ, <br />
     *        or Connection.TRANSACTION_SERIALIZABLE.
     * @throws ConnectionException database error
     */
    void adjustTransactionIsolationLevel(int level) throws ConnectionException;

    /**
     * Closes the wrapped connection.
     * @throws ConnectionException connection error
     */
    void close() throws ConnectionException;
    
    /**
     * Closes the wrapped connection without throwing an exception.
     */
    void closeQuietly();
}
