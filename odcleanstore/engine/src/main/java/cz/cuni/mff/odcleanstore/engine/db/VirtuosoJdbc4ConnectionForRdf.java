package cz.cuni.mff.odcleanstore.engine.db;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

/**
 * Non-static methods are not thread-safe.
 * @author Jan Petr Jerman
 */
public final class VirtuosoJdbc4ConnectionForRdf {
 
    /** Database connection. */
    private Connection connection;
    
	public static VirtuosoJdbc4ConnectionForRdf createCleanDbConnection() throws ConnectionException {
		return new VirtuosoJdbc4ConnectionForRdf(ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials());
	}
	
	public static VirtuosoJdbc4ConnectionForRdf createDirtyDbConnection() throws ConnectionException {
		return new VirtuosoJdbc4ConnectionForRdf(ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials());
	}

    /**
     * Create a new instance.
     * @see #createConnection(JDBCConnectionCredentials)
     * @param connection a connection to a Virtuoso database
     */
    private VirtuosoJdbc4ConnectionForRdf(JDBCConnectionCredentials connectionCredentials) throws ConnectionException {
    	try {
            Class.forName("virtuoso.jdbc3.Driver");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Couldn't load Virtuoso jdbc4 driver", e);
        }
        try {
            connection = DriverManager.getConnection(
                    connectionCredentials.getConnectionString(),
                    connectionCredentials.getUsername(),
                    connectionCredentials.getPassword());
        
            		CallableStatement statement = connection.prepareCall(String.format(Locale.ROOT, "log_enable(%d)", 1));
            		statement.execute();
            		connection.setAutoCommit(false);
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
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
		execute(String.format(Locale.ROOT, "SPARQL INSERT INTO GRAPH %s { %s %s %s }", graphName, subject, predicate, object));
	}
	
	/**
	 * Delete graph from the database.
	 * @param graphName name of the graph to delete
	 * @throws QueryException query error  
	 */
	public void clearGraph(String graphName) throws QueryException {
		execute(String.format(Locale.ROOT, "SPARQL CLEAR GRAPH %s", graphName));
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

		executeCall(statement);
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

		executeCall(statement);
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
     * Closes the connection.
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
    
    /**
     * Executes a general SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @param objects query bindings
     * @throws QueryException query error
     */
    private void execute(String query, Object... objects) throws QueryException {
        try {
            PreparedStatement statement = connection.prepareStatement(query);

            for (int i = 0; i < objects.length; ++i) {
                statement.setObject(i + 1, objects[i]);
            }
            
            statement.execute();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
     
    /**
     * Executes a long durable callable SQL/SPARQL query.
     * @param query SQL/SPARQL query
     * @throws QueryException query error
     */
    private void executeCall(String query) throws QueryException {
        try {
            CallableStatement cst = connection.prepareCall(query);
            cst.execute();
        } catch (SQLException e) {
            throw new QueryException(e);
        }
    }
}
