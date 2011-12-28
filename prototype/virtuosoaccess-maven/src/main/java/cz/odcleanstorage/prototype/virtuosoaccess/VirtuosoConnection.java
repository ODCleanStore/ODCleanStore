package cz.odcleanstorage.prototype.virtuosoaccess;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

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
	 * Creates a new connections to the database. 
	 * 
	 * @param connectString
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
	 * Commit changes to the database. 
	 * 
	 * @throws SQLException 
	 */
	public void commit() throws SQLException
	{
		con.commit();
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
    	String stat = "{call DB.DBA.RDF_QUAD_URI('" + graph + "', '" + subject + "', '"  + predicate + "', '"  + object + "')}";
    	CallableStatement cst = con.prepareCall(stat);
    	cst.execute();	
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
	public void insertRdfXml(String relativeBase, String rdfXml, String graph) throws SQLException
	{
		String stat = relativeBase != null ?
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '" + relativeBase +  "', '" + graph + "')}" :
				"{call DB.DBA.RDF_LOAD_RDFXML('" + rdfXml + "', '' , '" + graph + "')}" ;
		
		CallableStatement cst = con.prepareCall(stat);
		cst.execute();
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
	public void executeStatement(String statement, RowListener rowListener) throws SQLException
	{
		Statement stmt = con.createStatement();
		stmt.execute(statement);
		ResultSetMetaData data = stmt.getResultSet().getMetaData();
		
		boolean more = true;		
		while(more)	{
			ResultSet rs = stmt.getResultSet();
			while(rs.next()) {
				String[] row = new String[data.getColumnCount()];
				for(int i = 0; i < row.length; i++)	{
					row[i] = rs.getString(i +1); 
				}
				rowListener.processRow(this, row);
			}
			more = stmt.getMoreResults();
		}
	}
}
