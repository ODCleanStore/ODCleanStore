package cz.cuni.mff.odcleanstore.shared;

import java.sql.SQLException;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;

public class UniqueGraphNameGenerator implements UniqueURIGenerator {
	
	/*
	public static void main (String[] args) {
		System.err.println(new UniqueGraphNameGenerator("http://opendata.cz/data/", new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba")).nextURI());
		System.err.println(new UniqueGraphNameGenerator("http://example.com/", new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba")).nextURI());
		System.err.println(new UniqueGraphNameGenerator("http://opendata.cz/data/namedGraph/", new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1112/UID=dba/PWD=dba", "dba", "dba")).nextURI());
	}
	*/
	
	private static final String uriSuffixQueryFormat = "SPARQL SELECT bif:atoi(fn:replace(str(?graph), \"%s(.*)$\", \"$1\")) AS ?suffix WHERE {GRAPH ?graph {?s ?p ?o}} GROUP BY ?graph ORDER BY DESC(?suffix) LIMIT 1";

	private String uriBase;
	private JDBCConnectionCredentials connectionCredentials;
	private VirtuosoConnectionWrapper connection;

	private VirtuosoConnectionWrapper getConnection () throws ConnectionException {
        if (connection == null) {
        	connection = VirtuosoConnectionWrapper.createConnection(connectionCredentials);
       	}
		return connection;
	}

	private void closeConnection() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (ConnectionException e) {
		} finally {
			connection = null;
		}
	}
	
	public UniqueGraphNameGenerator (String uriBase, JDBCConnectionCredentials connectionCredentials) {
		this.uriBase = uriBase;
		this.connectionCredentials = connectionCredentials;
	}

	@Override
	public String nextURI() {
		return nextURI(0);
	}

	public String nextURI(Integer start) {
		try
		{
			//System.err.println(String.format(uriSuffixQueryFormat, uriBase));
			WrappedResultSet resultSet = getConnection().executeSelect(String.format(uriSuffixQueryFormat, uriBase));
			
			Integer id = -1;
			
			if (resultSet.next()) {
				Integer maxId = resultSet.getInt("suffix");
				
				if (maxId != null) {
					id = maxId;
				}
			}
			
			return uriBase + (start + id + 1);
		} catch (ConnectionException e) {
		} catch (QueryException e) {
		} catch (SQLException e) {
		} finally {
			closeConnection();
		}

		return null;
	}

}
