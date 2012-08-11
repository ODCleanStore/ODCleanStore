package cz.cuni.mff.odcleanstore.qualityassessment.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cz.cuni.mff.odcleanstore.configuration.BackendConfig;
import cz.cuni.mff.odcleanstore.configuration.ConfigLoader;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.DatabaseException;
import cz.cuni.mff.odcleanstore.connection.JDBCConnectionCredentials;
import cz.cuni.mff.odcleanstore.qualityassessment.exceptions.QualityAssessmentException;

import java.sql.ResultSet;
import java.sql.Blob;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import virtuoso.jdbc3.VirtuosoDataSource;
import virtuoso.jena.driver.VirtModel;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Rules Model.
 *
 * Facilitates changes and queries for quality assessment rules.
 *
 * @author Jakub Daniel
 */
public class QualityAssessmentRulesModel {
	public static void main(String[] args) {
		try {
			ConfigLoader.loadConfig();
			BackendConfig config = ConfigLoader.getConfig().getBackendGroup();
			
			JDBCConnectionCredentials credentials = config.getCleanDBJDBCConnectionCredentials();
			VirtuosoDataSource dataSource = new VirtuosoDataSource();
			dataSource.setServerName(credentials.getConnectionString());
			dataSource.setUser(credentials.getUsername());
			dataSource.setPassword(credentials.getPassword());

			new QualityAssessmentRulesModel(dataSource).compileOntologyToRules("public-contracts", "Group 1");
		} catch (Exception e) {
			System.err.println(e.getMessage());
			
			e.printStackTrace();
		}
	}
	
	private static final String ruleByGroupIdQueryFormat = "SELECT id, groupId, filter, coefficient, description FROM " +
			"DB.ODCLEANSTORE.QA_RULES WHERE groupId = ?";
	private static final String ruleByGroupLabelQueryFormat = "SELECT rules.id AS id," +
			"rules.groupId AS groupId," +
			"rules.filter AS filter," +
			"rules.coefficient AS coefficient," +
			"rules.description AS description FROM " +
			"DB.ODCLEANSTORE.QA_RULES AS rules JOIN " +
			"DB.ODCLEANSTORE.QA_RULES_GROUPS AS groups ON rules.groupId = groups.id " +
			"WHERE groups.label = ?";
	private static final String groupIdQuery = "SELECT id FROM DB.ODCLEANSTORE.QA_RULES_GROUPS WHERE label = ?";
	private static final String ontologyIdQuery = "SELECT id FROM DB.ODCLEANSTORE.ONTOLOGIES WHERE label = ?";
	private static final String ontologyGraphURIQuery = "SELECT graphName FROM DB.ODCLEANSTORE.ONTOLOGIES WHERE id = ?";
	private static final String ontologyResourceQuery = "SELECT ?s WHERE {?s ?p ?o} GROUP BY ?s";
	private static final String deleteRulesByOntology = "DELETE FROM DB.ODCLEANSTORE.QA_RULES WHERE groupId IN " +
			"(SELECT groupId FROM DB.ODCLEANSTORE.QA_RULES_GROUPS_TO_ONTOLOGIES_MAP WHERE ontologyId = ?)";
	private static final String deleteMapping = "DELETE FROM DB.ODCLEANSTORE.QA_RULES_GROUPS_TO_ONTOLOGIES_MAP WHERE groupId = ? AND ontologyId = ? ";
	private static final String insertRule = "INSERT INTO DB.ODCLEANSTORE.QA_RULES (groupId, filter, coefficient, description) VALUES (?, ?, ?, ?)";
	private static final String mapGroupToOntology = "INSERT INTO DB.ODCLEANSTORE.QA_RULES_GROUPS_TO_ONTOLOGIES_MAP (groupId, ontologyId) VALUES (?, ?)";

	private static final Logger LOG = LoggerFactory.getLogger(QualityAssessmentRulesModel.class);

	private JDBCConnectionCredentials endpoint;
	
	/**
	 * Connection to dirty database (needed in all cases to work on a new graph or a copy of an existing one)
	 */
	private VirtuosoConnectionWrapper cleanConnection;

	/**
	 * constructs new connection to the dirty database.
	 * 
	 * @return wrapped connection to the dirty database
	 * @throws DatabaseException
	 */
	private VirtuosoConnectionWrapper getCleanConnection () throws DatabaseException {
        if (cleanConnection == null) {
        	cleanConnection = VirtuosoConnectionWrapper.createConnection(endpoint);
       	}
		return cleanConnection;
	}

	/**
	 * makes sure the connection to the dirty database is closed and not referenced
	 */
	private void closeCleanConnection() {
		try {
			if (cleanConnection != null) {
				cleanConnection.close();
			}
		} catch (DatabaseException e) {
		} finally {
			cleanConnection = null;
		}
	}
	
	public QualityAssessmentRulesModel (JDBCConnectionCredentials endpoint) {
		this.endpoint = endpoint;
	}
	
	public QualityAssessmentRulesModel (VirtuosoDataSource dataSource) {
		this.endpoint = new JDBCConnectionCredentials(
				dataSource.getServerName(),
				dataSource.getUser(),
				dataSource.getPassword());
	}
	
	private Collection<QualityAssessmentRule> queryRules (String query, Object... objects) throws QualityAssessmentException {
		Collection<QualityAssessmentRule> rules = new ArrayList<QualityAssessmentRule>();
		
		try {
			WrappedResultSet results = getCleanConnection().executeSelect(query, objects);
			
			/**
			 * Fill the collection with rule instances for all records in database.
			 */
			while (results.next()) {
				ResultSet result = results.getCurrentResultSet();
				
				Long id = result.getLong("id");
				
				Long groupId = result.getLong("groupId");
				
				Blob filterBlob = result.getBlob("filter");
				String filter = new String(filterBlob.getBytes(1, (int)filterBlob.length()));
				
				Double coefficient = result.getDouble("coefficient");
				
				Blob descriptionBlob = result.getBlob("description");
				String description = new String(descriptionBlob.getBytes(1, (int)descriptionBlob.length()));
				
				rules.add(new QualityAssessmentRule(id, groupId, filter, coefficient, description));
			}
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		} catch (SQLException e) {
			throw new QualityAssessmentException(e);
		} finally {
			closeCleanConnection();
		}
		
		return rules;
	}
	
	/**
     * @param groupIds IDs of the rule groups from which the rules are selected
     */
	public Collection<QualityAssessmentRule> getRules (Integer... groupIds) throws QualityAssessmentException {
		Set<QualityAssessmentRule> rules = new HashSet<QualityAssessmentRule>();
		
		for (int i = 0; i < groupIds.length; ++i) {
			Collection<QualityAssessmentRule> groupSpecific = queryRules(ruleByGroupIdQueryFormat, groupIds[i]);
			
			rules.addAll(groupSpecific);
		}

		return rules;
	}
	
	/**
     * @param groupLabels set of labels of groups from which the rules are selected
     */
	public Collection<QualityAssessmentRule> getRules (String... groupLabels) throws QualityAssessmentException {
		Set<QualityAssessmentRule> rules = new HashSet<QualityAssessmentRule>();
		
		for (int i = 0; i < groupLabels.length; ++i) {
			Collection<QualityAssessmentRule> groupSpecific = queryRules(ruleByGroupLabelQueryFormat, groupLabels[i]);
			
			rules.addAll(groupSpecific);
		}
		
		return rules;
	}
	
	private Long getGroupId(String groupLabel) throws QualityAssessmentException {
		try {
			WrappedResultSet resultSet = getCleanConnection().executeSelect(groupIdQuery, groupLabel);
			
			if (!resultSet.next()) throw new QualityAssessmentException("No '" + groupLabel + "' QA Rule group."); 
		
			return resultSet.getCurrentResultSet().getLong("id");
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		} catch (SQLException e) {
			throw new QualityAssessmentException(e);
		}
	}
	
	private Long getOntologyId(String ontologyLabel) throws QualityAssessmentException {
		try {
			WrappedResultSet resultSet = getCleanConnection().executeSelect(ontologyIdQuery, ontologyLabel);
			
			if (!resultSet.next()) throw new QualityAssessmentException("No '" + ontologyLabel + "' ontology."); 
			
			return resultSet.getCurrentResultSet().getLong("id");
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		} catch (SQLException e) {
			throw new QualityAssessmentException(e);
		}
	}
	
	private String getOntologyGraphURI(Long ontologyId) throws QualityAssessmentException {
		try {
			WrappedResultSet resultSet = getCleanConnection().executeSelect(ontologyGraphURIQuery, ontologyId);
		
			if (!resultSet.next()) throw new QualityAssessmentException("No ontology with id " + ontologyId + "."); 
			
			return resultSet.getCurrentResultSet().getString("graphName");
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		} catch (SQLException e) {
			throw new QualityAssessmentException(e);
		}
	}
	
	private void mapGroupToOntology(Long groupId, Long ontologyId) throws QualityAssessmentException {
		try {
			getCleanConnection().execute(mapGroupToOntology, groupId, ontologyId);
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		}
	}
	
	public void compileOntologyToRules(String ontologyLabel, String groupLabel) throws QualityAssessmentException {
		try {
			Long groupId = getGroupId(groupLabel);
			Long ontologyId = getOntologyId(ontologyLabel);
		
			compileOntologyToRules(ontologyId, groupId);
		} finally {
			closeCleanConnection();
		}
	}
	
	public void compileOntologyToRules(Long ontologyId, Long groupId) throws QualityAssessmentException {
		try {
			String ontologyGraphURI = getOntologyGraphURI(ontologyId);

			VirtModel ontology = VirtModel.openDatabaseModel(ontologyGraphURI,
					endpoint.getConnectionString(),
					endpoint.getUsername(),
					endpoint.getPassword());
		
			QueryExecution query = QueryExecutionFactory.create(ontologyResourceQuery, ontology);
		
			com.hp.hpl.jena.query.ResultSet resultSet = query.execSelect();
		
			dropRules(groupId, ontologyId);
		
			mapGroupToOntology(groupId, ontologyId);
		
			while (resultSet.hasNext()) {
				QuerySolution solution = resultSet.next();
			
				processOntologyResource(solution.getResource("s"), ontology, ontologyGraphURI, groupId);
			}
		} finally {
			closeCleanConnection();
		}
	}
	
	private void dropRules(Long groupId, Long ontologyId) throws QualityAssessmentException {
		try {			
			getCleanConnection().execute(deleteRulesByOntology, ontologyId);
			getCleanConnection().execute(deleteMapping, groupId, ontologyId);
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		}
	}
	
	private void processOntologyResource(Resource resource, Model model, String ontology, Long groupId) throws QualityAssessmentException {
		final String skosNS = "http://www.w3.org/2004/02/skos/core#";

		/**
		 * Functional Property can have only 1 value
		 */
		if (model.contains(resource, RDF.type, OWL.FunctionalProperty)) {	
			QualityAssessmentRule rule = new QualityAssessmentRule(null, groupId, "{?s <" + resource.getURI() + "> ?o} GROUP BY ?s HAVING COUNT(?o) > 1", 0.8, resource.getLocalName() + " is FunctionalProperty (can have only 1 unique value)");
			
			storeRule(rule, ontology);
		}

		/**
		 * Value of Inverse Functional Property cannot be shared by two or more subjects
		 */
		if (model.contains(resource, RDF.type, OWL.InverseFunctionalProperty)) {
			QualityAssessmentRule rule = new QualityAssessmentRule(null, groupId, "{?s <" + resource.getURI() + "> ?o} GROUP BY ?o HAVING COUNT(?s) > 1", 0.8, resource.getLocalName() + " is InverseFunctionalProperty (value cannot be shared by two distinct subjects)");
			
			storeRule(rule, ontology);
		}

		/**
		 * Enumeration check based on Concept Scheme
		 */
		if (model.contains(resource, RDF.type, model.createProperty(skosNS, "ConceptScheme"))) {
			StmtIterator enumerations = model.listStatements(resource, model.createProperty(skosNS, "hasTopConcept"), model.getRDFNode(Node.ANY));

			/**
			 * Generate list of possible values
			 */
			StringBuilder valueList = new StringBuilder();
			StringBuilder filter = new StringBuilder();
			
			while (enumerations.hasNext()) {	
				Statement conceptStmt = enumerations.next();

				if (model.contains(conceptStmt.getObject().asResource(), RDF.type, model.createProperty(skosNS, "Concept"))) {
					valueList.append(conceptStmt.getObject().asNode().getURI());
					filter.append("?o != <" + conceptStmt.getObject().asNode().getURI() + ">");
					
					if (enumerations.hasNext()) {
						valueList.append(", ");
						filter.append(" AND ");
					}
				} else {
					throw new QualityAssessmentException("Missing definition of a Concept in ConceptScheme");
				}
			}

			/**
			 * Generate rules for all properties with enumerated range
			 */
			QueryExecution queryExecution = QueryExecutionFactory.create("SELECT ?s WHERE {?s <" + RDFS.range + "> [<" + OWL.hasValue + "> <" + resource.getURI() + ">].}", model);
			
			com.hp.hpl.jena.query.ResultSet resultSet = queryExecution.execSelect();
			
			while (resultSet.hasNext()) {
				QuerySolution solution = resultSet.nextSolution();

				QualityAssessmentRule rule = new QualityAssessmentRule(null, groupId, "{?s <" + solution.get("s") + "> ?o. FILTER (" + filter + ")}", 0.8, solution.getResource("s").getLocalName() + " can have only these values: " + valueList.toString());

				storeRule(rule, ontology);
			}
		}
	}
	
	private void storeRule (QualityAssessmentRule rule, String ontology) throws QualityAssessmentException {
		try{
			getCleanConnection().execute(insertRule, rule.getGroupId(), rule.getFilter(), rule.getCoefficient(), rule.getDescription());
			
			LOG.info("Generated quality assessment rule from ontology " + ontology);
		} catch (DatabaseException e) {
			throw new QualityAssessmentException(e);
		}
	}
}
