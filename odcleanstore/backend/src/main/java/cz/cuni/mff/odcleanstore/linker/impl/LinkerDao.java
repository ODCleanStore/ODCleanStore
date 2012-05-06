package cz.cuni.mff.odcleanstore.linker.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

public class LinkerDao {
	
	private static LinkerDao dao;
	private static VirtuosoConnectionWrapper connection;
	
	private LinkerDao(SparqlEndpoint endpoint) throws ConnectionException {
		connection = VirtuosoConnectionWrapper.createConnection(endpoint);
	}
	
	public static LinkerDao getInstance(SparqlEndpoint endpoint) throws ConnectionException {
		if (dao == null) {
			return new LinkerDao(endpoint);
		}
		return dao;
	}
	
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
	
	private String createInPart(String[] groups) {
		String result = "(";
		for (String group : groups) {
			result += group + ",";
		}
		return result.substring(0, result.length()-1) + ")";
	}
}
