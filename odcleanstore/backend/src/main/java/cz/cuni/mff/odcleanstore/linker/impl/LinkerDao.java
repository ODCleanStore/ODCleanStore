package cz.cuni.mff.odcleanstore.linker.impl;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionFactory;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.QueryException;
import cz.cuni.mff.odcleanstore.data.EnumDatabaseInstance;
import cz.cuni.mff.odcleanstore.data.TableVersion;
import cz.cuni.mff.odcleanstore.linker.rules.FileOutput;
import cz.cuni.mff.odcleanstore.linker.rules.Output;
import cz.cuni.mff.odcleanstore.linker.rules.OutputType;
import cz.cuni.mff.odcleanstore.linker.rules.SilkRule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

	private static final int IN_COUNT_LIMIT = 500;

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
	public List<SilkRule> loadRules(Integer[] groups, TableVersion tableVersion)
			throws QueryException, ConnectionException {
	    if (groups == null || groups.length == 0) {
	        LOG.info("Loaded 0 linkage rules.");
	        return Collections.<SilkRule>emptyList();
	    }

		List<SilkRule> ruleList = new ArrayList<SilkRule>();
		VirtuosoConnectionWrapper connection = null;
		WrappedResultSet resultSet = null;
		String tableName = "DB.ODCLEANSTORE.OI_RULES" + tableVersion.getTableSuffix();
		try {
			connection = VirtuosoConnectionFactory.createJDBCConnection(cleanDBCredentials);
			resultSet = connection.executeSelect(
					"select id, label, linkType, sourceRestriction, targetRestriction, blob_to_string(linkageRule) as rule, filterThreshold, filterLimit " +
					"from " + tableName + " where groupId in " + createInPart(groups));
			while (resultSet.next()) {
				SilkRule rule = createRule(resultSet);
				rule.setOutputs(loadOutputs(connection, resultSet.getInt("id"), tableVersion));
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
		LOG.info("Loaded {} linkage rules: {}", ruleList.size(), ruleList.toString());
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

	private List<Output> loadOutputs(VirtuosoConnectionWrapper connection, Integer ruleId, TableVersion tableVersion )
			throws QueryException, SQLException {
		List<Output> outputs = new ArrayList<Output>();
		WrappedResultSet resultSet = null;
		String tableName = "DB.ODCLEANSTORE.OI_OUTPUTS" + tableVersion.getTableSuffix();
		try {
		    String query = "select t.label as type, o.minConfidence, o.maxConfidence, o.fileName, f.label as format " +
                    "from " + tableName + " o join DB.ODCLEANSTORE.OI_OUTPUT_TYPES t on o.outputTypeId = t.id " +
                    "left join DB.ODCLEANSTORE.OI_FILE_FORMATS f on o.fileFormatId = f.id " +
                    "where o.ruleId = " + ruleId;
			resultSet = connection.executeSelect(query);
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
	private String createInPart(Integer[] members) {
		String result = "(";
		for (Integer member : members) {
			result += member + ",";
		}
		return result.substring(0, result.length()-1) + ")";
	}

	/**
	 * Deletes given graph from the RDF database.
	 *
	 * @param graphId ID of a graph to delete
	 * @throws ConnectionException
	 * @throws QueryException
	 */
	public void clearGraph(String graphId) throws ConnectionException, QueryException {
		LOG.info("Clearing graph: {} ", graphId);
		VirtuosoConnectionWrapper connection = null;
		try {
			connection = VirtuosoConnectionFactory.createJDBCConnection(dirtyDBCredentials);
			connection.execute("SPARQL CLEAR GRAPH <" + graphId + ">");
		} finally {
			if (connection != null) {
				connection.closeQuietly();
			}
		}
	}

	/**
	 * Loads labels (rdfs:label) for given URIs.
	 *
	 * @param uriLabelMap in/out parameter, map URI:label
	 * @throws QueryException
	 * @throws ConnectionException
	 */
	public void loadLabels(Map<String, String> uriLabelMap) throws QueryException, ConnectionException {
		if (uriLabelMap == null || uriLabelMap.isEmpty()) {
			LOG.info("No URIs to load labels for.");
			return;
		}

		Iterator<String> uriIt = uriLabelMap.keySet().iterator();
		VirtuosoConnectionWrapper connection = null;
		WrappedResultSet resultSet = null;
		try {
			connection = VirtuosoConnectionFactory.createJDBCConnection(cleanDBCredentials);
			while (uriIt.hasNext()) {
				String uriList = createUriListString(uriIt);
				LOG.info("Loading labels for URIs: {}", uriList);
				resultSet = connection.executeSelect(
					"SPARQL SELECT ?uri ?label WHERE {?uri <http://www.w3.org/2000/01/rdf-schema#label> ?label " +
					"FILTER (?uri IN " + uriList + ")}");
				while (resultSet.next()) {
					uriLabelMap.put(resultSet.getString("uri"), resultSet.getString("label"));
				}
				if (resultSet != null) {
					resultSet.closeQuietly();
				}
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

	private String createUriListString(Iterator<String> iterator) {
		int count = 0;
		String result = "(";
		while (iterator.hasNext() && count < IN_COUNT_LIMIT) {
			result += "<" + iterator.next() + ">, ";
			count++;
		}
		return result.substring(0, result.length()-2) + ")";
	}

	/**
	 * Creates a graph group (metagraph) in clean or dirty database and adds graphs to it.
	 *
	 * @param groupName name of the group to be created
	 * @param graphNames graphs to be added to the group
	 * @param db database instance (clean/dirty)
	 * @throws ConnectionException
	 * @throws QueryException
	 */
	public void createGraphGroup(String groupName, Set<String> graphNames, EnumDatabaseInstance db)
			throws ConnectionException, QueryException {
		String createQuery = "DB.DBA.RDF_GRAPH_GROUP_CREATE(?, 0)";
		String insertQuery = "DB.DBA.RDF_GRAPH_GROUP_INS(?, ?)";
		VirtuosoConnectionWrapper connection = null;
		try {
			if (EnumDatabaseInstance.CLEAN.equals(db)) {
				connection = VirtuosoConnectionFactory.createJDBCConnection(cleanDBCredentials);
			} else {
				connection = VirtuosoConnectionFactory.createJDBCConnection(dirtyDBCredentials);
			}
			connection.execute(createQuery, groupName);
			for (String graphName: graphNames) {
				connection.execute(insertQuery, groupName, graphName);
			}
		} finally {
			if (connection != null) {
				connection.closeQuietly();
			}
		}
	}

	/**
	 * Deletes given graph group.
	 *
	 * @param groupName name of group to be deleted
	 * @param db database instance (clean/dirty)
	 * @param isSilent when set to false and group does not exist, exception is raised
	 * @throws ConnectionException
	 * @throws QueryException
	 */
	public void deleteGraphGroup(String groupName, EnumDatabaseInstance db, boolean isSilent)
			throws ConnectionException, QueryException {
		String query = "DB.DBA.RDF_GRAPH_GROUP_DROP(?, ?)";
		VirtuosoConnectionWrapper connection = null;
		try {
			if (EnumDatabaseInstance.CLEAN.equals(db)) {
				connection = VirtuosoConnectionFactory.createJDBCConnection(cleanDBCredentials);
			} else {
				connection = VirtuosoConnectionFactory.createJDBCConnection(dirtyDBCredentials);
			}
			connection.execute(query, groupName, isSilent ? 1 : 0);
		} finally {
			if (connection != null) {
				connection.closeQuietly();
			}
		}
	}

	/**
	 * Load all RDF graphs' names from the clean database
	 *
	 * @return list of graphs' names
	 * @throws QueryException
	 * @throws ConnectionException
	 */
	public Set<String> getAllGraphNames() throws QueryException, ConnectionException {
		String query = "DB.DBA.SPARQL_SELECT_KNOWN_GRAPHS()";
		VirtuosoConnectionWrapper connection = null;
		WrappedResultSet resultSet = null;
		try {
			connection = VirtuosoConnectionFactory.createJDBCConnection(cleanDBCredentials);
			resultSet = connection.executeSelect(query);
			Set<String> result = new HashSet<String>();
			while (resultSet.next()) {
				result.add(resultSet.getString("GRAPH_IRI"));
			}
			return result;
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
