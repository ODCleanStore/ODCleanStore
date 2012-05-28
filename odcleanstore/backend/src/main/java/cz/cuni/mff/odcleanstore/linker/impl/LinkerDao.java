package cz.cuni.mff.odcleanstore.linker.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.ConnectionCredentials;

/**
 * A singleton class for loading linkage rules from the relational DB.
 * 
 * @author Tomas Soukup
 */
public class LinkerDao {
	private static final Logger LOG = LoggerFactory.getLogger(LinkerDao.class);
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
	 * @param credentials connection parameters
	 * @throws ConnectionException
	 */
	private LinkerDao(ConnectionCredentials credentials) throws ConnectionException {
		LOG.info("Connecting to DB on: " + credentials.getUri());
		connection = VirtuosoConnectionWrapper.createConnection(credentials);
	}
	
	
	/**
	 * Creates singleton instance.
	 * 
	 * @param credentials connection parameters
	 * @return singleton instance
	 * @throws ConnectionException
	 */
	public static LinkerDao getInstance(ConnectionCredentials credentials) throws ConnectionException {
		if (dao == null) {
			return new LinkerDao(credentials);
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
		WrappedResultSet resultSet = connection.executeSelect("select blob_to_string(definition) from DB.ODCLEANSTORE.OI_RULES where groupId in "
																+ createInPart(groups));
		while (resultSet.next()) {
			ruleList.add(resultSet.getString(1));
		}
		resultSet.closeQuietly();
		LOG.info("Loaded {} linkage rules.", ruleList.size());
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
