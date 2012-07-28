/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.common;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

/**
 * Encapsulates jdbc connections and single threaded basic data operations on Virtuoso database.
 * 
 * @author Petr Jerman
 */
public class SimpleVirtuosoAccess {

	/**
	 * Create a new connections.
	 * 
	 * @throws ClassNotFoundException
	 * @throws QueryException
	 * @throws ConnectionException 
	 */
	public static SimpleVirtuosoAccess createCleanDBConnection() throws ConnectionException {
		JDBCConnectionCredentials credit = ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();

		return new SimpleVirtuosoAccess(credit);
	}

	public static SimpleVirtuosoAccess createDirtyDBConnection() throws ConnectionException {
		JDBCConnectionCredentials credit = ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials();

		return new SimpleVirtuosoAccess(credit);	
	}

	private VirtuosoConnectionWrapper _con;

	/**
	 * Create a new connections to the database.
	 * 
	 * @param connectionString
	 * @param user
	 * @param password
	 * @throws ConnectionException 
	 */
	private SimpleVirtuosoAccess(JDBCConnectionCredentials connectionCredentials) throws ConnectionException {
		_con = VirtuosoConnectionWrapper.createConnection(connectionCredentials);
		_con.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
	}

	/**
	 * Close connection
	 * 
	 */
	public void close() {
		if (_con != null) {
			_con.closeQuietly();
		}
	}

	/**
	 * Commit changes to the database.
	 * 
	 * @throws QueryException
	 * @throws SQLException 
	 */
	public void commit() throws SQLException {
		_con.commit();
	}

	/**
	 * Delete graph from the database.
	 * 
	 * @param graphName
	 * 
	 * @throws QueryException 
	 */
	public void deleteGraph(String graphName) throws QueryException {
		String statement = String.format("SPARQL CLEAR GRAPH %s", graphName);
		_con.execute(statement);
	}

	/**
	 * Insert quad to the database.
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param graph
	 * 
	 * @throws QueryException
	 */
	public void insertQuad(String subject, String predicate, String object, String graph) throws QueryException {
		String statement = String.format("SPARQL INSERT INTO GRAPH %s { %s %s %s }", graph, subject, predicate, object);
		_con.execute(statement);
	}
	
	/**
	 * Insert rdfXml or ttl to the database.
	 * 
	 * @param relativeBase
	 * @param rdfXml or Ttl
	 * @param graph
	 * 
	 * @throws QueryException
	 */
	public void insertRdfXmlOrTtl(String relativeBase, String payload, String graph) throws QueryException {
		if (payload.startsWith("<?xml")){
			insertRdfXml(relativeBase, payload, graph);
		}
		else {
			insertTtl(relativeBase, payload, graph);
		}
	}

	/**
	 * Insert rdfXml to the database.
	 * 
	 * @param relativeBase
	 * @param rdfXml
	 * @param graph
	 * 
	 * @throws QueryException
	 * @throws QueryException 
	 */
	public void insertRdfXml(String relativeBase, String rdfXml, String graph) throws QueryException {
		String statement = relativeBase != null ?
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '" + relativeBase + "', '" + graph + "')}" :
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '' , '"	+ graph + "')}";

		_con.executeLongDurableCall(statement);
	}
	
	/**
	 * Insert TTL to the database.
	 * 
	 * @param relativeBase
	 * @param Ttl data
	 * @param graph
	 * 
	 * @throws QueryException
	 * @throws QueryException 
	 */
	public void insertTtl(String relativeBase, String ttl, String graph) throws QueryException {
		String statement = relativeBase != null ?
				"{call DB.DBA.TTLP('" + ttl + "', '" + relativeBase + "', '" + graph + "', 0)}" :
				"{call DB.DBA.TTLP('" + ttl + "', '' , '"	+ graph + "', 0)}";

		_con.executeLongDurableCall(statement);
	}


	/**
	 * Execute Sql statement with processing returned rows.
	 * 
	 * @param statement
	 * 
	 * @throws QueryException
	 * @throws SQLException 
	 */
	public Collection<String[]> getRowFromSqlStatement(String statement) throws QueryException, SQLException {
		LinkedList<String[]> retVal = new LinkedList<String[]>();

		WrappedResultSet wrs = _con.executeSelect(statement);
		
		while (wrs.next()) {
			ResultSet rs = wrs.getCurrentResultSet();
			String[] row = new String[rs.getMetaData().getColumnCount()];
			for (int i = 0; i < row.length; i++) {
				row[i] = rs.getString(i + 1);
			}
			retVal.add(row);
		}
		return retVal;
	}

	/**
	 * Execute Sql statement with processing rows by rowListener.
	 * 
	 * @param statement
	 * 
	 * @throws QueryException
	 * @throws SQLException 
	 */
	public void processSqlStatementRows(String statement, RowListener rowListener) throws QueryException, SQLException {
		
		WrappedResultSet wrs = _con.executeSelect(statement);
		while (wrs.next()) {
			ResultSet rs = wrs.getCurrentResultSet();
			rowListener.processRow(rs, rs.getMetaData());
		}
	}
	
	/**
	 * Execute Sql and Sparql statement.
	 * 
	 * @param statement
	 * 
	 * @throws SQLException
	 * @throws QueryException 
	 */
	public void executeStatement(String statement) throws QueryException {
		_con.execute(statement);
	}

	/**
	 * Revert changes to the last commit.
	 * 
	 * @throws QueryException
	 * @throws SQLException 
	 */
	public void revert() throws SQLException {
		_con.rollback();
	}

	/**
	 * Overridden finalize method close connection.
	 */
	@Override
	protected void finalize() {
		try {
			close();
			super.finalize();
		} catch (Throwable e) {
		}
	}
}