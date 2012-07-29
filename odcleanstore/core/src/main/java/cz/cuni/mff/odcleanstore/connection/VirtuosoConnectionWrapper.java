package cz.cuni.mff.odcleanstore.connection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

/**
 * A wrapper for SQL {@link Connection} to a Virtuoso database.
 * Wraps {@link SQLException} an ODCleanStoreException and serves as a factory class
 * for {@link WrappedResultSet}.
 *
 * Non-static methods are not thread-safe.
 * @author Jan Michelfeit
 */
public final class VirtuosoConnectionWrapper {
	// Changed by Petr Jerman - loading from global config
    /**
     * Query timeout in seconds.
     * Loaded from global configuration settings.
     */
    private static int query_timeout = -1;
    
    /**
     * Create a new connection and return its wrapper.
     * Should be used only for connection to a Virtuoso instance.
     * @param connectionCredentials connection settings for the SPARQL endpoint
     * @return wrapper of the newly created connection
     * @throws ConnectionException database connection error
     */
    public static VirtuosoConnectionWrapper createConnection(JDBCConnectionCredentials connectionCredentials) throws ConnectionException {
    	// Added by Petr Jerman - loading from global config 
    	if(query_timeout < 0) {
    		query_timeout = ConfigLoader.getConfig().getBackendGroup().getQueryTimeout();
    	}
    	// End added by Petr Jerman
        try {
            Class.forName("virtuoso.jdbc3.Driver");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Couldn't load Virtuoso jdbc driver", e);
        }
        try {
            Connection connection = DriverManager.getConnection(
                    connectionCredentials.getConnectionString(),
                    connectionCredentials.getUsername(),
                    connectionCredentials.getPassword());
            return new VirtuosoConnectionWrapper(connection);
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }
 
    /** Database connection. */
    private Connection connection;

    /**
     * Create a new instance.
     * @see #createConnection(JDBCConnectionCredentials)
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
            statement.setQueryTimeout(query_timeout);
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

            statement.setQueryTimeout(query_timeout);
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
            statement.setQueryTimeout(query_timeout);
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
            statement.setQueryTimeout(query_timeout);
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

            statement.setQueryTimeout(query_timeout);
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
    
    // Added by Petr Jerman
    
    /**
     * Create a new connection for transactional level processing and return its wrapper.
     * Should be used only for connection to a Virtuoso instance.
     * @param connectionCredentials connection settings for the SPARQL endpoint
     * @return wrapper of the newly created connection
     * @throws ConnectionException database connection error
     */
    public static VirtuosoConnectionWrapper createTransactionalLevelConnection(JDBCConnectionCredentials connectionCredentials) throws ConnectionException {
    	VirtuosoConnectionWrapper virtuosoConnectionWrapper = createConnection(connectionCredentials);
    	virtuosoConnectionWrapper.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
    	return virtuosoConnectionWrapper;
    }
     
    /**
     * Executes a long durable callable SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @throws QueryException query error
     */
    public void executeLongDurableCall(String query) throws QueryException {
        try {
            CallableStatement cst = connection.prepareCall(query);
            cst.execute();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
    
	/**
	 * Delete graph from the database.
	 * @param graphName name of the graph to delete
	 * @throws QueryException query error  
	 */
	public void deleteGraph(String graphName) throws QueryException {
		String statement = String.format("SPARQL CLEAR GRAPH %s", graphName);
		execute(statement);
	}

	/**
	 * Insert a quad to the database.
	 * @param subject subject part of the quad to insert
	 * @param predicate predicate part of the quad to insert
	 * @param object object part of the quad to insert
	 * @param graphName graph name part of the quad to insert
	 * @throws QueryException query error
	 */
	public void insertQuad(String subject, String predicate, String object, String graphName) throws QueryException {
		String statement = String.format("SPARQL INSERT INTO GRAPH %s { %s %s %s }", graphName, subject, predicate, object);
		execute(statement);
	}
	
	/**
	 * Insert RDF data in RdfXml or Ttl format to the database.
	 * @param relativeBase relative URI base for payload
	 * @param payload payload in RdfXml or Ttl format
	 * @param graphName name of the graph to insert
	 * @throws QueryException query error
	 */
	public void insertRdfXmlOrTtl(String relativeBase, String payload, String graphName) throws QueryException {
		if (payload.startsWith("<?xml")){
			insertRdfXml(relativeBase, payload, graphName);
		}
		else {
			insertTtl(relativeBase, payload, graphName);
		}
	}

	/**
	 * Insert RDF data in rdfXml format to the database.
	 * @param relativeBase relative URI base for payload
	 * @param rdfXml payload in RdfXml format
	 * @param graphName name of the graph to insert
	 * @throws QueryException query error
	 */
	public void insertRdfXml(String relativeBase, String rdfXml, String graphName) throws QueryException {
		String statement = relativeBase != null ?
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '" + relativeBase + "', '" + graphName + "')}" :
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '' , '"	+ graphName + "')}";

		executeLongDurableCall(statement);
	}
	
	/**
	 * Insert RDF data in TTL format to the database.
	 * @param relativeBase relative URI base for payload
	 * @param ttl payload in Ttl format
	 * @param graphName name of the graph to insert
	 * @throws QueryException query error
	 */
	public void insertTtl(String relativeBase, String ttl, String graphName) throws QueryException {
		String statement = relativeBase != null ?
				"{call DB.DBA.TTLP('" + ttl + "', '" + relativeBase + "', '" + graphName + "', 0)}" :
				"{call DB.DBA.TTLP('" + ttl + "', '' , '"	+ graphName + "', 0)}";

		executeLongDurableCall(statement);
	}
    // End added by Petr Jerman
}
