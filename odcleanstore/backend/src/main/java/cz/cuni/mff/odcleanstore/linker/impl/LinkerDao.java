package cz.cuni.mff.odcleanstore.linker.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

/**
 * A singleton class for loading linkage rules from the relational DB.
 * 
 * @author Tomas Soukup
 */
public class LinkerDao {
	
	/**
	 * singleton instance
	 */
	private static LinkerDao dao;
	
	/**
	 * singleton connection instance
	 */
	private static VirtuosoConnectionWrapper connection;
	
	/**
	 * Private constructor used by the getInstance method.
	 * 
	 * @param endpoint endpoint with connection parameters
	 * @throws ConnectionException
	 */
	private LinkerDao(SparqlEndpoint endpoint) throws ConnectionException {
		connection = VirtuosoConnectionWrapper.createConnection(endpoint);
	}
	
	
	/**
	 * Creates singleton instance.
	 * 
	 * @param endpoint endpoint with connection parameters
	 * @return singleton instance
	 * @throws ConnectionException
	 */
	public static LinkerDao getInstance(SparqlEndpoint endpoint) throws ConnectionException {
		if (dao == null) {
			return new LinkerDao(endpoint);
		}
		return dao;
	}
	
	/**
	 * Loads rules from given groups from the database.
	 * 
	 * @param groups array of group IDs
	 * @return list of loaded linkage rules
	 * @throws QueryException
	 * @throws SQLException
	 */
	public List<String> loadRules(String[] groups) throws QueryException, SQLException {
		List<String> ruleList = new ArrayList<String>();
		WrappedResultSet resultSet = connection.executeSelect("select blob_to_string(definition) from DB.FRONTEND.OI_RULES where groupId in "
																+ createInPart(groups));
		while (resultSet.next()) {
			ruleList.add(resultSet.getString(1));
		}
		resultSet.closeQuietly();
		
		return ruleList;
	}
	
	/**
	 * Creates the IN part of SQL query from list of group IDs
	 * 
	 * @param groups list of group IDs
	 * @return IN part in format (id1,id2,...)
	 */
	private String createInPart(String[] groups) {
		String result = "(";
		for (String group : groups) {
			result += group + ",";
		}
		return result.substring(0, result.length()-1) + ")";
	}
}
