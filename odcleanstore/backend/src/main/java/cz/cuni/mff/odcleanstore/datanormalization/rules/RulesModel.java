package cz.cuni.mff.odcleanstore.datanormalization.rules;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;

public class RulesModel {
	private static final Logger LOG = LoggerFactory.getLogger(RulesModel.class);

	private JDBCConnectionCredentials endpoint;
	
	public RulesModel (JDBCConnectionCredentials endpoint) {
		this.endpoint = endpoint;
	}
	
	private Collection<Rule> queryRules (String query, Object... objects) throws DataNormalizationException {
		Map<Integer, Rule> rules = new HashMap<Integer, Rule>();
		
		VirtuosoConnectionWrapper connection = null;
		WrappedResultSet results = null;
		
		try {
			connection = VirtuosoConnectionWrapper.createConnection(endpoint);
			results = connection.executeSelect(query, objects);
			
			/**
			 * Fill the collection with rule instances for all records in database.
			 */
			while (results.next()) {
				ResultSet result = results.getCurrentResultSet();
				
				Integer id = result.getInt("id");
				
				Blob typeBlob = result.getBlob("type");
				String type = new String(typeBlob.getBytes(1, (int)typeBlob.length()));
								
				Blob modificationBlob = result.getBlob("modification");
				String modification = new String(modificationBlob.getBytes(1, (int)modificationBlob.length()));
				
				if (rules.containsKey(id)) {
					Rule rule = rules.get(id);
					
					rule.addComponent(type, modification);
				} else {
					rules.put(id, new Rule(id, type, modification));
				}
			}
		} catch (DatabaseException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (SQLException e) {
			throw new DataNormalizationException(e.getMessage());
		} finally {
			if (results != null) {
				results.closeQuietly();
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (ConnectionException e) {
					LOG.error("Rules Model connection not closed: " + e.getMessage());
				}
			}
		}
		
		return rules.values();
	}
	
	/**
     * @param groupIds IDs of the rule groups from which the rules are selected
     */
	public Collection<Rule> getRules (Integer... groupIds) throws DataNormalizationException {
		Set<Rule> rules = new HashSet<Rule>();
		
		for (int i = 0; i < groupIds.length; ++i) {
			Collection<Rule> groupSpecific = queryRules("SELECT rules.id AS id, " +
					"components.type AS type, " +
					"components.modification AS modification FROM " +
					"DB.ODCLEANSTORE.DN_RULES AS rules JOIN " +
					"DB.ODCLEANSTORE.DN_RULE_COMPONENTS AS components ON components.ruleId = rules.id " +
					"WHERE groupId = ?", groupIds[i]);
			
			rules.addAll(groupSpecific);
		}
		
		return rules;
	}
	
	/**
     * @param groupLabels set of labels of groups from which the rules are selected
     */
	public Collection<Rule> getRules (String... groupLabels) throws DataNormalizationException {
		Set<Rule> rules = new HashSet<Rule>();
		
		for (int i = 0; i < groupLabels.length; ++i) {
			Collection<Rule> groupSpecific = queryRules("SELECT rules.id AS id, " +
					"components.type AS type, " +
					"components.modification AS modification FROM " +
					"DB.ODCLEANSTORE.DN_RULES AS rules JOIN " +
					"DB.ODCLEANSTORE.DN_RULES_GROUPS AS groups ON rules.groupId = groups.id JOIN " +
					"DB.ODCLEANSTORE.DN_RULE_COMPONENTS AS components ON components.ruleId = rules.id " +
					"WHERE groups.label = ?", groupLabels[i]);
			
			rules.addAll(groupSpecific);
		}
		
		return rules;
	}
}
