/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.common;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;

import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;

/**
 * Encapsulates jdbc connections and single threaded basic data operations on Virtuoso database.
 * 
 * @author Petr Jerman
 */
public class SimpleVirtuosoAccess {

	private static boolean _isDriverInitialized = false;

	/**
	 * Create a new connections to the local database on 1111 port with dba credentials.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static SimpleVirtuosoAccess createCleanDBConnection() throws ClassNotFoundException, SQLException {
		JDBCConnectionCredentials credit = ConfigLoader.getConfig().getBackendGroup().getCleanDBJDBCConnectionCredentials();

		return new SimpleVirtuosoAccess(credit.getConnectionString(), credit.getUsername(), credit.getPassword());
	}

	public static SimpleVirtuosoAccess createDirtyDBConnection() throws ClassNotFoundException, SQLException {
		JDBCConnectionCredentials credit = ConfigLoader.getConfig().getBackendGroup().getDirtyDBJDBCConnectionCredentials();

		return new SimpleVirtuosoAccess(credit.getConnectionString(), credit.getUsername(), credit.getPassword());	}

	private Connection _con;

	/**
	 * Create a new connections to the database.
	 * 
	 * @param connectionString
	 * @param user
	 * @param password
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public SimpleVirtuosoAccess(String connectionString, String user, String password) throws ClassNotFoundException, SQLException {
		if (!_isDriverInitialized) {
			Class.forName("virtuoso.jdbc3.Driver");
			_isDriverInitialized = true;
		}

		_con = DriverManager.getConnection(connectionString, user, password);

		adjustTransactionLevel("1", false);
	}

	/**
	 * Close connection
	 * 
	 */
	public void close() {

		if (_con != null) {
			try {
				_con.close();
				_con = null;
			} catch (SQLException e) {
			}
		}
	}

	/**
	 * Commit changes to the database.
	 * 
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
	 * @throws SQLException
	 */
	public void deleteGraph(String graphName) throws SQLException {
		String statement = String.format("SPARQL CLEAR GRAPH %s", graphName);
		executeStatement(statement);
	}

	/**
	 * Insert quad to the database.
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param graph
	 * 
	 * @throws SQLException
	 */
	public void insertQuad(String subject, String predicate, String object, String graph) throws SQLException {
		String statement = String.format("SPARQL INSERT INTO GRAPH %s { %s %s %s }", graph, subject, predicate, object);
		executeStatement(statement);
	}

	/**
	 * Insert rdfXml to the database.
	 * 
	 * @param relativeBase
	 * @param rdfXml
	 * @param graph
	 * 
	 * @throws SQLException
	 */
	public void insertRdfXml(String relativeBase, String rdfXml, String graph) throws SQLException {
		String stat = relativeBase != null ?
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '" + relativeBase + "', '" + graph + "')}" :
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '' , '"	+ graph + "')}";

		CallableStatement cst = _con.prepareCall(stat);
		cst.execute();
	}

	/**
	 * Execute Sql and Sparql statement.
	 * 
	 * @param statement
	 * 
	 * @throws SQLException
	 */
	public void executeStatement(String statement) throws SQLException {
		Statement stmt = _con.createStatement();
		stmt.execute(statement);
	}

	/**
	 * Execute Sql statement with processing returned rows.
	 * 
	 * @param statement
	 * 
	 * @throws SQLException
	 */
	public Collection<String[]> getRowFromSqlStatement(String statement) throws SQLException {
		LinkedList<String[]> retVal = new LinkedList<String[]>();

		Statement stmt = _con.createStatement();
		stmt.execute(statement);
		if (stmt.getResultSet() != null) {
			ResultSetMetaData data = stmt.getResultSet().getMetaData();
			boolean more = true;
			while (more) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					String[] row = new String[data.getColumnCount()];
					for (int i = 0; i < row.length; i++) {
						row[i] = rs.getString(i + 1);
					}
					retVal.add(row);
				}
				more = stmt.getMoreResults();
			}
		}

		return retVal;
	}

	/**
	 * Execute Sql statement with processing rows by rowListener.
	 * 
	 * @param statement
	 * 
	 * @throws SQLException
	 */
	public void processSqlStatementRows(String statement, RowListener rowListener) throws SQLException {
		Statement stmt = _con.createStatement();
		stmt.execute(statement);
		if (stmt.getResultSet() != null) {
			ResultSetMetaData data = stmt.getResultSet().getMetaData();
			boolean more = true;
			while (more) {
				ResultSet rs = stmt.getResultSet();
				while (rs.next()) {
					rowListener.processRow(rs, data);
				}
				more = stmt.getMoreResults();
			}
		}
	}

	/**
	 * Revert changes to the last commit.
	 * 
	 * @throws SQLException
	 */
	public void revert() throws SQLException {
		_con.rollback();
	}

	/**
	 * Adjust transaction level in Virtuoso and jdbc
	 * 
	 * @param virtusoLogEnableValue
	 *            - 0 disable log, 1 enable transaction level log, 3 enable statement level log
	 * @param autoCommitValue
	 * 
	 * @throws SQLException
	 */
	private void adjustTransactionLevel(String virtusoLogEnableValue, boolean autoCommitValue) throws SQLException {

		CallableStatement cst = _con.prepareCall(String.format("log_enable(%s)", virtusoLogEnableValue));
		cst.execute();
		_con.setAutoCommit(autoCommitValue);
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