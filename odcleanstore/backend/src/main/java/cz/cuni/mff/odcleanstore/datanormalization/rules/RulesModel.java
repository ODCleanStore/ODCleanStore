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

import com.hp.hpl.jena.graph.Node;
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
	
	private static final String ruleByGroupIdQueryFormat = "SELECT rules.id AS id, " +
			"rules.groupId AS groupId, " +
			"types.label AS type, " +
			"components.modification AS modification, " +
			"rules.description AS description, " +
			"components.description AS componentDescription FROM " +
			"DB.ODCLEANSTORE.DN_RULES AS rules JOIN " +
			"DB.ODCLEANSTORE.DN_RULE_COMPONENTS AS components ON components.ruleId = rules.id JOIN " +
			"DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES AS types ON components.typeId = types.id " +
			"WHERE groupId = ?";
	private static final String ruleByGroupLabelQueryFormat = "SELECT rules.id AS id, " +
			"rules.groupId AS groupId, " +
			"types.label AS type, " +
			"components.modification AS modification " +
			"rules.description AS description, " +
			"components.description AS componentDescription FROM " +
			"DB.ODCLEANSTORE.DN_RULES AS rules JOIN " +
			"DB.ODCLEANSTORE.DN_RULES_GROUPS AS groups ON rules.groupId = groups.id JOIN " +
			"DB.ODCLEANSTORE.DN_RULE_COMPONENTS AS components ON components.ruleId = rules.id JOIN " +
			"DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES AS types ON components.typeId = types.id " +
			"WHERE groups.label = ?";
	private static final String ontologyResourceQueryFormat = "SELECT ?s WHERE {?s ?p ?o} GROUP BY ?s";
	private static final String deleteRulesByOntologyFormat = "DELETE FROM DB.ODCLEANSTORE.DN_RULES WHERE id IN " +
			"(SELECT ruleId AS id FROM DB.ODCLEANSTORE.DN_RULES_TO_ONTOLOGIES_MAP WHERE ontology = ?)";
	private static final String insertConvertedPropertyValueFormat = "{?s <%s> ?x} WHERE {GRAPH $$graph$$ {SELECT ?s <%s>(str(?o)) AS ?x WHERE {?s <%s> ?o}}}";
	private static final String deleteUnconvertedPropertyValueFormat = "{?s <%s> ?o} WHERE {GRAPH $$graph$$ {?s <%s> ?o. FILTER (?o != <%s>(str(?o)))}}";
	private static final String insertRuleFormat = "INSERT INTO DB.ODCLEANSTORE.DN_RULES (groupId, description) VALUES (?, ?)";
	private static final String lastIdQueryFormat = "SELECT identity_value() AS id";
	private static final String insertComponentFormat = "INSERT INTO DB.ODCLEANSTORE.DN_RULE_COMPONENTS (ruleId, typeId, modification, description) " +
			"SELECT ? AS ruleId, id AS typeId, ? AS modification, ? AS description FROM DB.ODCLEANSTORE.DN_RULE_COMPONENT_TYPES WHERE label = ?";
	private static final String mapRuleToOntologyFormat = "INSERT INTO DB.ODCLEANSTORE.DN_RULES_TO_ONTOLOGIES_MAP (ruleId, ontology) VALUES (?, ?)";

	private static final Logger LOG = LoggerFactory.getLogger(RulesModel.class);

	private JDBCConnectionCredentials endpoint;
	
	public RulesModel (JDBCConnectionCredentials endpoint) {
		this.endpoint = endpoint;
	}
	
	/**
	 * selects all rules that satisfy conditions of the query. It is required that the query is projected to
	 * id (int), groupId (int), type (string), modification (string), description (string), componentDescription (string)
	 * @param query the select query
	 * @param objects the bindings to the query
	 * @return a collection of the selected rules
	 * @throws DataNormalizationException
	 */
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
			throw new DataNormalizationException(e);
		} catch (SQLException e) {
			throw new DataNormalizationException(e);
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
	 * selects rules that belong to groups whose IDs are among groupIds
     * @param groupIds IDs of the rule groups from which the rules are selected
     * @return a collection of the selected rules
     */
	public Collection<Rule> getRules (Integer... groupIds) throws DataNormalizationException {
		Set<Rule> rules = new HashSet<Rule>();
		
		for (int i = 0; i < groupIds.length; ++i) {
			Collection<Rule> groupSpecific = queryRules(ruleByGroupIdQueryFormat, groupIds[i]);
			
			rules.addAll(groupSpecific);
		}
		
		return rules;
	}
	
	/**
	 * selects rules that belong to groups whose labels are among groupLabels
     * @param groupLabels set of labels of groups from which the rules are selected
     * @return a collection of the selected rules
     */
	public Collection<Rule> getRules (String... groupLabels) throws DataNormalizationException {
		Set<Rule> rules = new HashSet<Rule>();
		
		for (int i = 0; i < groupLabels.length; ++i) {
			Collection<Rule> groupSpecific = queryRules(ruleByGroupLabelQueryFormat, groupLabels[i]);
			
			rules.addAll(groupSpecific);
		}
		
		return rules;
	}

	/**
	 * creates rules that verify properties of the input ontology (stored in the clean database)
	 * @param ontologyUri the uri of the ontology whose properties should be verified by the ouput rules
	 * @param groupId the ID of a rule group to which the new rules should be stored 
	 * @throws DataNormalizationException
	 */
	public void compileOntologyToRules(String ontologyUri, Integer groupId) throws DataNormalizationException {
		VirtModel ontology = VirtModel.openDatabaseModel(ontologyUri,
				endpoint.getConnectionString(),
				endpoint.getUsername(),
				endpoint.getPassword());
		
		QueryExecution query = QueryExecutionFactory.create(ontologyResourceQueryFormat, ontology);
		
		com.hp.hpl.jena.query.ResultSet resultSet = query.execSelect();
		
		/**
		 * Remove all the rules generated from this ontology
		 */
		dropRules(ontologyUri);
		
		/**
		 * Process all resources in the ontology
		 */
		while (resultSet.hasNext()) {
			QuerySolution solution = resultSet.next();
			
			processOntologyResource(solution.getResource("s"), ontology, ontologyUri, groupId);
		}
	}

	/**
	 * removes all rules that were created according to this ontology
	 * @param ontology the uri of the ontology to which the deleted rules are to be mapped
	 * @throws DataNormalizationException
	 */
	private void dropRules(String ontology) throws DataNormalizationException {
		VirtuosoConnectionWrapper connection = null;
		
		try {
			connection = VirtuosoConnectionWrapper.createConnection(endpoint);
			
			connection.execute(deleteRulesByOntologyFormat, ontology);
			
		} catch (DatabaseException e) {
			throw new DataNormalizationException(e);
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

	/**
	 * examines one resource and creates rule(s) for it 
	 * @param resource the resource to be examined
	 * @param model the ontology model
	 * @param ontology the name of the ontology (URI)
	 * @param groupId the ID of the rule group to store the rules to
	 * @throws DataNormalizationException
	 */
	private void processOntologyResource(Resource resource, Model model, String ontology, Integer groupId) throws DataNormalizationException {
		if (model.contains(resource, RDFS.range, model.asRDFNode(Node.ANY))) {
			boolean knownConversion = false;

			String insert = null;
			String delete = null;
			String datatype = null;
			
			/**
			 * Correct string
			 */
			if (model.contains(resource, RDFS.range, XSD.xstring)) {
				knownConversion = true;

				insert = String.format(insertConvertedPropertyValueFormat, resource.getURI(), XPathFunctions.stringFunction, resource.getURI());
				delete = String.format(deleteUnconvertedPropertyValueFormat, resource.getURI(), resource.getURI(), XPathFunctions.stringFunction);
				datatype = XSD.xstring.getLocalName();
			}
			
			/**
			 * Correct date formats ("YYYY" to "YYYY-MM-DD" etc.)
			 */
			if (model.contains(resource, RDFS.range, XSD.date)) {
				knownConversion = true;

				insert = String.format(insertConvertedPropertyValueFormat, resource.getURI(), XPathFunctions.dateFunction, resource.getURI());
				delete = String.format(deleteUnconvertedPropertyValueFormat, resource.getURI(), resource.getURI(), XPathFunctions.dateFunction);
				datatype = XSD.date.getLocalName();
			}

			if (knownConversion) {
				Rule rule = new Rule(null, groupId, "Convert " + resource.getLocalName() + " into " + datatype,
						"INSERT",
						insert,
						"Create proper " + datatype + " value for the property " + resource.getURI(),
						
						"DELETE",
						delete,
						"Remove all improper values of the property " + resource.getURI());
				
				storeRule(rule, ontology);
			}
		}
	}

	/**
	 * stores a generated rule and maps it to the ontology to be able to track its origin and dependence on the ontology
	 * @param rule the rule to be stored
	 * @param ontology the ontology the rule should be mapped to
	 * @throws DataNormalizationException
	 */
	private void storeRule (Rule rule, String ontology) throws DataNormalizationException {
		VirtuosoConnectionWrapper connection = null;
		
		try {
			connection = VirtuosoConnectionWrapper.createConnection(endpoint);
			
			connection.adjustTransactionLevel(EnumLogLevel.TRANSACTION_LEVEL, false);
			
			connection.execute(insertRuleFormat, rule.getGroupId(), rule.getDescription());
			
			Component[] components = rule.getComponents();
			
			Integer id = 0;
			WrappedResultSet result = connection.executeSelect(lastIdQueryFormat);
			
			if (result.next()) {
				id = result.getInt("id");
			} else {
				throw new DataNormalizationException("Failed to bind rule component to rule.");
			}
			
			for (int i = 0; i < components.length; ++i) {
				connection.execute(insertComponentFormat,
						id, components[i].getModification(), components[i].getDescription(), components[i].getType().toString());
			}
			
			connection.execute(mapRuleToOntologyFormat, id, ontology);
			
			connection.commit();

			LOG.info("Generated data normalization rule from ontology " + ontology);
		} catch (DatabaseException e) {
			e.printStackTrace(System.err);
			throw new DataNormalizationException(e);
		} catch (QueryException e) {
			throw new DataNormalizationException(e);
		} catch (SQLException e) {
			throw new DataNormalizationException(e);
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
