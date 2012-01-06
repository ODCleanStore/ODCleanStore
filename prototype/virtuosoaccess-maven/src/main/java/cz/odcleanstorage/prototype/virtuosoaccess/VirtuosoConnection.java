package cz.odcleanstorage.prototype.virtuosoaccess;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import virtuoso.jdbc4.VirtuosoExtendedString;
import virtuoso.jdbc4.VirtuosoRdfBox;
import virtuoso.jdbc4.VirtuosoResultSet;

/**
 * Encapsulates jdbc connections and single threaded basic data operations on Virtuoso database.
 * 
 * @author Petr Jerman (petr.jerman@centrum.cz)
 *
 */
public class VirtuosoConnection
{
	private static boolean isDriverInitialized = false;
	
	private Connection con; 
	
	/**
	 * Create a new connections to the database. 
	 * 
	 * @param connectionString
	 * @param user
	 * @param password 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public VirtuosoConnection(String connectionString, String user, String password) throws ClassNotFoundException, SQLException
	{
		if (!isDriverInitialized) {
			Class.forName("virtuoso.jdbc4.Driver");
			isDriverInitialized = true;
		}
		
		con = DriverManager.getConnection(connectionString, user, password);
		
		con.setAutoCommit(false);
	}
	
	/**
	 * Adjust transaction level in Virtuoso and jdbc  
	 * 
	 * @param virtusoLogEnableValue - 0 disable log, 1 enable transaction level log, 3 enable statement level log
	 * @param autoCommitValue
  	 *
	 * @throws SQLException 
	 */
	public void adjustTransactionLevel(String virtusoLogEnableValue, boolean autoCommitValue) throws SQLException {
		
		CallableStatement cst = con.prepareCall(String.format("log_enable(%s)", virtusoLogEnableValue));
		cst.execute();
		con.setAutoCommit(autoCommitValue);
	}
	
	/**
	 * Create a new connections to the local database on 1111 port with dba/dba credentials. 
	 * 
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static VirtuosoConnection CreateInitialVirtuosoDbaConnection() throws ClassNotFoundException, SQLException {
		
		return new VirtuosoConnection("jdbc:virtuoso://localhost:1111","dba","dba"); 
	}
	
	/**
	 * Close connection 
	 * 
	 * @throws SQLException 
	 */
	public void close() {
		if (con != null) {
			try {
				con.close();
				con = null;
			}
			catch(SQLException e) {
			}
		}
	}
	
	protected void finalize() throws Throwable
	{
	  close();	
	  super.finalize(); 
	}

	/**
	 * Commit changes to the database. 
	 * 
	 * @throws SQLException 
	 */
	public void commit() throws SQLException
	{
		con.commit();
	}
	
	/**
	 * Revert changes to the last commit. 
	 * 
	 * @throws SQLException 
	 */
	public void revert() throws SQLException
	{
		con.rollback();
	}
	
	/**
	 * Read quads for graph. 
	 * 
	 * @param forGraph
	 * @param rowListener
	 *    
	 * @return count of rows
	 *    
	 * @throws SQLException 
	 */
	public long readQuads(String forGraph, RowListener rowListener) throws SQLException
	{
		String statement = String.format("SPARQL SELECT ?s ?p ?o %s WHERE { GRAPH %s {?s ?p ?o} }", forGraph, forGraph);
		return executeStatement(statement, rowListener);
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
	public void insertQuad(String subject, String predicate, String object, String graph) throws SQLException
	{
		String statement = String.format("SPARQL INSERT INTO GRAPH %s { %s %s %s }", graph, subject, predicate, object);
		executeStatement(statement);
	}

	/**
	 * Delete quad from the database. 
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @param graph
	 *    
	 * @throws SQLException 
	 */	
	public void deleteQuads(String subject, String predicate, String object, String graph) throws SQLException
	{
		String statement = String.format("SPARQL DELETE FROM GRAPH %s { %s %s %s }", graph, subject, predicate, object);
		executeStatement(statement);
	}

	/**
	 * Execute Sql and Sparql statement.
	 * 
	 * @param statement
	 *    
	 * @throws SQLException 
	 */	
	public void executeStatement(String statement) throws SQLException
	{
		Statement stmt = con.createStatement();
        stmt.execute(statement);
	}

	/**
	 * Execute Sql and Sparql statement with processing returned rows. 
	 * 
	 * @param statement
	 *    
	 * @throws SQLException 
	 */	
	public long executeStatement(String statement, RowListener rowListener) throws SQLException
	{
		long retCount = 0;
		
		Statement stmt = con.createStatement();
		stmt.execute(statement);
		ResultSetMetaData data = stmt.getResultSet().getMetaData();
		
		boolean more = true;		
		while(more)	{
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
				String[] row = new String[data.getColumnCount()];
				for(int i = 0; i < row.length; i++)	{
					
					String s = rs.getString(i + 1);
					Object o = ((VirtuosoResultSet)rs).getObject(i + 1);

					if (o instanceof VirtuosoExtendedString) { 
					    VirtuosoExtendedString vs = (VirtuosoExtendedString) o;
			                    if (vs.iriType == VirtuosoExtendedString.IRI && (vs.strType & 0x1) == 0x1) {
			                    	row[i] ="<" + vs.str +">";
			                    }
			                    else {
			                    	row[i] = vs.str;
			                    }
					}
					else if (o instanceof VirtuosoRdfBox) {
					    VirtuosoRdfBox rb = (VirtuosoRdfBox) o;
					    row[i] = rb.rb_box.toString();
					}
					else if(stmt.getResultSet().wasNull()) {
					    row[i] = "NULL";
					}
					else {
						row[i] = s;
					}
				}
				retCount++;
				rowListener.processRow(this, row);
			}
			more = stmt.getMoreResults();
		}
		
		return retCount;
	}
}
