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

import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

import cz.cuni.mff.odcleanstore.connection.EnumLogLevel;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.datanormalization.exceptions.DataNormalizationException;
import cz.cuni.mff.odcleanstore.datanormalization.rules.Rule.Component;
import cz.cuni.mff.odcleanstore.vocabulary.XPathFunctions;

public class RulesModel {
	public static void main(String[] args) {
		try {
			new RulesModel(new JDBCConnectionCredentials("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba")).compileOntologyToRules("http://purl.org/procurement/public-contracts", 1);
		} catch (DataNormalizationException e) {
			System.err.println(e.getMessage());
		}
	}

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
				
				Integer groupId = result.getInt("groupId");
				
				Blob typeBlob = result.getBlob("type");
				String type = new String(typeBlob.getBytes(1, (int)typeBlob.length()));
								
				Blob modificationBlob = result.getBlob("modification");
				String modification = new String(modificationBlob.getBytes(1, (int)modificationBlob.length()));
				
				Blob descriptionBlob = result.getBlob("description");
				String description = new String(descriptionBlob.getBytes(1, (int)descriptionBlob.length()));
				
				Blob componentDescriptionBlob = result.getBlob("componentDescription");
				String componentDescription = new String(componentDescriptionBlob.getBytes(1, (int)componentDescriptionBlob.length()));
				
				if (rules.containsKey(id)) {
					Rule rule = rules.get(id);
					
					rule.addComponent(type, modification, componentDescription);
				} else {
					rules.put(id, new Rule(id, groupId, description, type, modification, componentDescription));
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
					"rules.groupId AS groupId, " +
					"types.label AS type, " +
					"components.modification AS modification, " +
					"rules.description AS description, " +
					"components.description AS componentDescription FROM " +
					"DB.ODCLEANSTORE.DN_RULES AS rules JOIN " +
					"DB.ODCLEANSTORE.DN_RULE_COMPONENTS AS components ON components.ruleId = rules.id JOIN " +
					"DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES AS types ON components.typeId = types.id " +
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
					"rules.groupId AS groupId, " +
					"types.label AS type, " +
					"components.modification AS modification " +
					"rules.description AS description, " +
					"components.description AS componentDescription FROM " +
					"DB.ODCLEANSTORE.DN_RULES AS rules JOIN " +
					"DB.ODCLEANSTORE.DN_RULES_GROUPS AS groups ON rules.groupId = groups.id JOIN " +
					"DB.ODCLEANSTORE.DN_RULE_COMPONENTS AS components ON components.ruleId = rules.id JOIN " +
					"DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES AS types ON components.typeId = types.id " +
					"WHERE groups.label = ?", groupLabels[i]);
			
			rules.addAll(groupSpecific);
		}
		
		return rules;
	}
	
	public void compileOntologyToRules(String ontologyUri, Integer groupId) throws DataNormalizationException {
		VirtModel ontology = VirtModel.openDatabaseModel(ontologyUri,
				endpoint.getConnectionString(),
				endpoint.getUsername(),
				endpoint.getPassword());
		
		QueryExecution query = QueryExecutionFactory.create("SELECT ?s WHERE {?s ?p ?o} GROUP BY ?s", ontology);
		
		com.hp.hpl.jena.query.ResultSet resultSet = query.execSelect();
		
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			
			processOntologyResource(solution.getResource("s"), ontology, ontologyUri, groupId);
		}
	}
	
	private void processOntologyResource(Resource resource, Model model, String ontology, Integer groupId) throws DataNormalizationException {
		if (model.contains(resource, RDFS.range, XSD.date)) {
			Rule rule = new Rule(null, groupId, "Convert " + resource.getLocalName() + " into " + XSD.date.getLocalName(),
					"INSERT",
					"{?s <" + resource.getURI() + "> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s <" + resource.getURI() + "> <" + XPathFunctions.dateFunction + ">(str(?o)) AS ?x WHERE {?s ?p ?o}}}",
					"Create proper " + XSD.date.getLocalName() + " value for the property " + resource.getURI(),
					
					"DELETE",
					"{?s <" + resource.getURI() + "> ?o} WHERE {GRAPH $$graph$$ {?s <" + resource.getURI() + "> ?o. FILTER (?o != <" + XPathFunctions.dateFunction + ">(str(?o)))}}",
					"Remove all improper values of the property " + resource.getURI());
			
			storeRule(rule, ontology);
		}
	}
	
	private void storeRule (Rule rule, String ontology) throws DataNormalizationException {
		VirtuosoConnectionWrapper connection = null;
		
		try {
			connection = VirtuosoConnectionWrapper.createConnection(endpoint);
			
			connection.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
			
			connection.execute(String.format("INSERT INTO DB.ODCLEANSTORE.DN_RULES (groupId, description) VALUES (%d, '%s')",
					rule.getGroupId(), rule.getDescription()));
			
			Component[] components = rule.getComponents();
			
			Integer id = 0;
			WrappedResultSet result = connection.executeSelect("SELECT identity_value() AS id");
			
			if (result.next()) {
				id = result.getInt("id");
			} else {
				throw new DataNormalizationException("Failed to bind rule component to rule.");
			}
			
			for (int i = 0; i < components.length; ++i) {
				connection.execute(String.format("INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (ruleId, typeId, modification, description) SELECT %d AS ruleId, id AS typeId, '%s' AS modification, '%s' AS description FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = '%s'",
						id, components[i].getModification(), components[i].getDescription(), components[i].getType().toString()));
			}
			
			connection.execute(String.format("INSERT INTO DB.ODCLEANSTORE.DN_RULES_TO_ONTOLOGIES_MAP (ruleId, ontology) VALUES (%d, '%s')", id, ontology));
			
			connection.commit();

			LOG.info("Generated data normalization rule from ontology " + ontology);
		} catch (DatabaseException e) {
			e.printStackTrace(System.err);
			throw new DataNormalizationException(e.getMessage());
		} catch (QueryException e) {
			throw new DataNormalizationException(e.getMessage());
		} catch (SQLException e) {
			throw new DataNormalizationException(e.getMessage());
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (ConnectionException e) {
					LOG.error("Rules Model connection not closed: " + e.getMessage());
				}
			}
		}
	}
}
