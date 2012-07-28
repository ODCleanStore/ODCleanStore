package cz.cuni.mff.odcleanstore.linker.impl;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.linker.rules.FileOutput;
import cz.cuni.mff.odcleanstore.linker.rules.Output;
import cz.cuni.mff.odcleanstore.linker.rules.OutputType;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	 * connection credentials to the clean DB
	 */
	private static JDBCConnectionCredentials cleanDBCredentials;
	
	/**
	 * connection credentials to the dirty DB
	 */
	private static JDBCConnectionCredentials dirtyDBCredentials;

	/**
	 * Private constructor used by the getInstance method.
	 *
	 * @param credentials connection parameters
	 * @throws ConnectionException
	 */
	private LinkerDao(JDBCConnectionCredentials cleanDBCredentials, JDBCConnectionCredentials dirtyDBCredentials) 
			throws ConnectionException {
		LinkerDao.cleanDBCredentials = cleanDBCredentials;
		LinkerDao.dirtyDBCredentials = dirtyDBCredentials;
	}


	/**
	 * Creates singleton instance.
	 *
	 * @param credentials connection parameters
	 * @return singleton instance
	 * @throws ConnectionException
	 */
	public static LinkerDao getInstance(JDBCConnectionCredentials cleanDBCredentials, JDBCConnectionCredentials dirtyDBCredentials) 
			throws ConnectionException {
		if (dao == null) {
			return new LinkerDao(cleanDBCredentials, dirtyDBCredentials);
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
	 * @throws ConnectionException 
	 */
	public List<SilkRule> loadRules(String[] groups) throws QueryException, ConnectionException {
		List<SilkRule> ruleList = new ArrayList<SilkRule>();
		VirtuosoConnectionWrapper connection = null;
		WrappedResultSet resultSet = null;
		try {
			connection = VirtuosoConnectionWrapper.createConnection(cleanDBCredentials);
			resultSet = connection.executeSelect(
					"select id, label, linkType, sourceRestriction, targetRestriction, blob_to_string(linkageRule) as rule, filterThreshold, filterLimit " +
					"from DB.ODCLEANSTORE.OI_RULES where groupId in " + createInPart(groups));
			while (resultSet.next()) {
				SilkRule rule = createRule(resultSet);
				rule.setOutputs(loadOutputs(connection, resultSet.getInt("id")));
				ruleList.add(rule);
			}
		} catch (SQLException se) {
			throw new QueryException(se);
		} finally {
			if (resultSet != null) {
				resultSet.closeQuietly();
			}
			if (connection != null) {
				connection.closeQuietly();
			}
		}		
		LOG.info("Loaded {} linkage rules.", ruleList.size());
		return ruleList;
	}
	
	private SilkRule createRule(WrappedResultSet resultSet) throws SQLException {
		SilkRule rule = new SilkRule();
		rule.setId(resultSet.getInt("id"));
		rule.setLabel(resultSet.getString("label"));
		rule.setLinkType(resultSet.getString("linkType"));
		rule.setSourceRestriction(resultSet.getString("sourceRestriction"));
		rule.setTargetRestriction(resultSet.getString("targetRestriction"));
		rule.setLinkageRule(resultSet.getString("rule"));
		rule.setFilterThreshold(resultSet.getBigDecimal("filterThreshold"));
		rule.setFilterLimit(resultSet.getInt("filterLimit"));
		return rule;
	}
	
	private List<Output> loadOutputs(VirtuosoConnectionWrapper connection, Integer ruleId) 
			throws QueryException, SQLException {
		List<Output> outputs = new ArrayList<Output>();
		WrappedResultSet resultSet = null;
		try {
			resultSet = connection.executeSelect(
					"select t.label as type, o.minConfidence, o.maxConfidence, o.fileName, f.label as format " +
					"from DB.ODCLEANSTORE.OI_OUTPUTS o join DB.ODCLEANSTORE.OI_OUTPUT_TYPES t on o.outputTypeId = t.id " +
					"join DB.ODCLEANSTORE.OI_FILE_FORMATS f on o.fileFormatId = f.id " +
					"where o.ruleId = " + ruleId);
			while (resultSet.next()) {
				Output output;
				String ruleType = resultSet.getString("type");
				if (OutputType.FILE.toString().equals(ruleType)) {
					FileOutput fileOutput = new FileOutput();
					fileOutput.setFormat(resultSet.getString("format"));
					fileOutput.setName(resultSet.getString("fileName"));
					output = fileOutput;
				} else {
					output = new Output();
				}
				output.setMinConfidence(resultSet.getBigDecimal("minConfidence"));
				output.setMaxConfidence(resultSet.getBigDecimal("maxConfidence"));
				outputs.add(output);
			}
		} finally {
			if (resultSet != null) {
				resultSet.closeQuietly();
			}
		}
		
		return outputs;
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
	
	public void clearGraph(String graphId) throws ConnectionException, QueryException {
		LOG.info("Clearing graph: {} ", graphId);
		VirtuosoConnectionWrapper connection = null;
		try {
			connection = VirtuosoConnectionWrapper.createConnection(dirtyDBCredentials);
			connection.execute("SPARQL CLEAR GRAPH <" + graphId + ">");
		} finally {
			if (connection != null) {
				connection.closeQuietly();
			}
		}
	}
	
	public void loadLabels(Map<String, String> uriLabelMap) throws QueryException, ConnectionException {
		LOG.info("Loading labels for URIs.");
		VirtuosoConnectionWrapper connection = null;
		WrappedResultSet resultSet = null;
		try {
			connection = VirtuosoConnectionWrapper.createConnection(cleanDBCredentials);
			resultSet = connection.executeSelect("SPARQL SELECT ...");
			while (resultSet.next()) {
				//TODO SELECT and result processing
			}
		} catch (SQLException e) {
			throw new QueryException(e);
		} finally {
			if (resultSet != null) {
				resultSet.closeQuietly();
			}
			if (connection != null) {
				connection.closeQuietly();
			}
		}
	}
}
