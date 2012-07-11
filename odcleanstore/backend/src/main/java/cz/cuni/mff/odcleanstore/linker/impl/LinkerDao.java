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
	private LinkerDao(JDBCConnectionCredentials credentials) throws ConnectionException {
		LOG.info("Connecting to DB on: " + credentials.getConnectionString());
		connection = VirtuosoConnectionWrapper.createConnection(credentials);
	}


	/**
	 * Creates singleton instance.
	 *
	 * @param credentials connection parameters
	 * @return singleton instance
	 * @throws ConnectionException
	 */
	public static LinkerDao getInstance(JDBCConnectionCredentials credentials) throws ConnectionException {
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
	public List<SilkRule> loadRules(String[] groups) throws QueryException, SQLException {
		List<SilkRule> ruleList = new ArrayList<SilkRule>();
		WrappedResultSet resultSet = connection.executeSelect(
				"select id, label, linkType, sourceRestriction, targetRestriction, blob_to_string(linkageRule) as rule, filterThreshold, filterLimit " +
				"from DB.ODCLEANSTORE.OI_RULES where groupId in " + createInPart(groups));
		while (resultSet.next()) {
			SilkRule rule = createRule(resultSet);
			rule.setOutputs(loadOutputs(resultSet.getInt("id")));
			ruleList.add(rule);
		}
		resultSet.closeQuietly();
		LOG.info("Loaded {} linkage rules.", ruleList.size());
		return ruleList;
	}
	
	private SilkRule createRule(WrappedResultSet resultSet) throws SQLException {
		SilkRule rule = new SilkRule();
		rule.setLabel(resultSet.getString("label"));
		rule.setLinkType(resultSet.getString("linkType"));
		rule.setSourceRestriction(resultSet.getString("sourceRestriction"));
		rule.setTargetRestriction(resultSet.getString("targetRestriction"));
		rule.setLinkageRule(resultSet.getString("rule"));
		rule.setFilterThreshold(resultSet.getBigDecimal("filterThreshold"));
		rule.setFilterLimit(resultSet.getInt("filterLimit"));
		return rule;
	}
	
	private List<Output> loadOutputs(Integer ruleId) throws QueryException, SQLException {
		List<Output> outputs = new ArrayList<Output>();
		WrappedResultSet resultSet = connection.executeSelect("select t.label as type, o.minConfidence, o.maxConfidence, o.fileName, f.label as format " +
				"from DB.ODCLEANSTORE.OI_OUTPUTS o inner join DB.ODCLEANSTORE.OI_OUTPUT_TYPES t on o.outputTypeId = t.id " +
				"inner join DB.ODCLEANSTORE.OI_FILE_FORMATS f on o.outputTypeId = f.id " +
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
}
