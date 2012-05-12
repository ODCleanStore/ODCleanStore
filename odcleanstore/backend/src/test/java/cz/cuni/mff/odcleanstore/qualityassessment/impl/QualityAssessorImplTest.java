package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import scala.actors.threadpool.Arrays;

import cz.cuni.mff.odcleanstore.TestUtils;
import cz.cuni.mff.odcleanstore.connection.VirtuosoConnectionWrapper;
import cz.cuni.mff.odcleanstore.connection.WrappedResultSet;
import cz.cuni.mff.odcleanstore.connection.exceptions.ConnectionException;
import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.qualityassessment.QualityAssessor;
import cz.cuni.mff.odcleanstore.transformer.EnumTransformationType;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraphException;
import cz.cuni.mff.odcleanstore.vocabulary.ODCS;
import cz.cuni.mff.odcleanstore.vocabulary.W3P;

class Triple {
	Object subject;
	Object predicate;
	Object object;
	
	private boolean isURL (String value) {
		try {
			new URL(value);
			
			return true;
		} catch (Exception e) {
		}
		
		return false;
	}
	
	private Object wrapLiteral (Object value) {

		if (value instanceof String) {
			if (isURL((String)value)) {
				return "<" + value + ">";
			} else {
				return "\"" + value + "\"";
			}
		}
		
		return value;
	}
	
	public Triple (Object subject, Object predicate, Object object) {

		this.subject = wrapLiteral(subject);
		this.predicate = wrapLiteral(predicate);
		this.object = wrapLiteral(object);
	}
}

class Graph {
	public String name = null;
	public List<Triple> triples = new ArrayList<Triple>();
	
	public Graph (Triple[] triples) {
		this.triples = Arrays.asList(triples);
	}
};

class TestInstance {
	public Graph graph;
	public Double score;
	public String[] trace;
	
	public TestInstance (Graph graph, Double score, String[] trace) {
		this.graph = graph;
		this.score = score;
		this.trace = trace;
	}
}

public class QualityAssessorImplTest extends TestCase {
	private SparqlEndpoint sparqlEndpoint;
	private VirtuosoConnectionWrapper connection;
	
	private TestInstance[] tests = new TestInstance[] {
		new TestInstance(
				new Graph(new Triple[] {
						new Triple("http://opendata.cz/data/f5178223-3b82-4af0-a832-9ba3652b23bf",
								"http://www.w3.org/1999/02/22-rdf-syntax-ns#type",
								"http://purl.org/procurement#Contract"),
						new Triple("http://opendata.cz/data/f5178223-3b82-4af0-a832-9ba3652b23bf",
								"http://purl.org/procurement#numberOfTenders",
								3),
						new Triple("http://opendata.cz/data/f5178223-3b82-4af0-a832-9ba3652b23bf",
								"http://purl.org/procurement#tender",
								"http://opendata.cz/data/aa37f947-b3f9-4fa7-9f9c-6c9d18c9f459")
				}),
				0.9,
				new String[] {
					"LIST OF TENDERS HAS DIFFERENT SIZE FROM WHAT WAS EXPECTED BY \'numberOfTenders\' PROPERTY"
				})

	};
	
	private String metadataGraphName;
	
	private List<Object[]> rulesRestrictionsBackup;
	private List<Object[]> domainsBackup;
	private List<Object[]> rulesBackup;
	
	private static final Object[][] domains = {
		{0, "http://opendata.cz"}
	};
	
	private static final Object[][] rulesRestrictions = {
		{1, 0}
	};
	
	private static final Object[][] rules = {
		{0, "{{?s <http://purl.org/procurement#referenceNumber> ?o} FILTER (bif:regexp_like(?o, \'[a-zA-Z]\'))}", 0.9, "PROCUREMENT REFERENCE NUMBER CONSISTS OF UNANTICIPATED CHARACTERS"},
		{1, "{{?s <http://purl.org/procurement#procedureType> ?o}} GROUP BY ?g ?s HAVING count(?o) > 1", 0.75, "PROCEDURE TYPE AMBIGUOUS"},
		{2, "{{?s <http://purl.org/procurement#tenderDeadline> ?d; <http://purl.org/procurement#endDate> ?e} FILTER (?e > ?d)}", 0.9, "TENDER COMPLETION DATE EXCEEDED ITS DEADLINE"},
		{3, "{{?s <http://purl.org/procurement#numberOfTenders> ?n. ?s <http://purl.org/procurement#tender> ?t}} GROUP BY ?g ?s ?n HAVING count(?t) != ?n", 0.9, "LIST OF TENDERS HAS DIFFERENT SIZE FROM WHAT WAS EXPECTED BY \'numberOfTenders\' PROPERTY"},
		{4, "{{?s <http://purl.org/procurement#contactPerson> ?c}} GROUP BY ?g HAVING count(?c) != 1", 0.8, "PROCUREMENT CONTACT PERSON MISSING"},
		{5, "{{?s <http://purl.org/procurement#lot> ?c; <http://purl.org/procurement#tender> ?t}}", 0.8, "PROCUREMENT BROKEN INTO SEVERAL CONTRACTS CANNOT HAVE DIRECT TENDERS"},
		{6, "{{?s <http://purl.org/procurement#name> ?n} FILTER (NOT bif:regexp_like(?n, \'^(doc. )?((Ing.|Mgr.|PhDr.|JUDr.|RNDr.|MUDr.|Bc.) )?(arch. )?[^., ]*( [^., ]*){1,2}(, (Ph.D.|CSc.))?$\'))}", 0.9, "NAME IN UNRECOGNIZABLE FORMAT"},
		{7, "{{?s <http://purl.org/procurement#estimatedPrice> ?p1; <http://purl.org/procurement#actualPrice> ?p2. ?p1 <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v1. ?p2 <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v2} FILTER (2 * ?v1 < ?v2)}", 0.8, "PROCUREMENT ACTUAL COSTS ARE ABOVE TWICE THE ESTIMATE"},
		{8, "{{?s <http://purl.org/procurement#procedureType> <http://purl.org/procurement#Open>; <http://purl.org/procurement#estimatedPrice> ?p. ?p <http://purl.org/goodrelations/v1#hasCurrencyValue> ?v.} FILTER (?v < 50000 OR ?v > 3000000)}", 0.8, "PROCEDURE TYPE IS INCOMPATIBLE WITH THE ESTIMATED PRICE"},
		{9, "{{?s <http://purl.org/procurement#awardDate> ?a; <http://purl.org/procurement#tenderDeadline> ?d.} FILTER (?d > ?a)}", 0.8, "TENDER AWARDED BEFORE APPLICATION DEADLINE"}};

	public QualityAssessorImplTest (String name) throws ConnectionException {
		super(name);
		
		sparqlEndpoint = new SparqlEndpoint("jdbc:virtuoso://localhost:1111/UID=dba/PWD=dba", "dba", "dba");
		connection = VirtuosoConnectionWrapper.createConnection(sparqlEndpoint);
		
		metadataGraphName = TestUtils.getUniqueURI();
	}
	
	private void backupRulesRestrictions() throws Exception {
		WrappedResultSet rulesRestrictions = connection.executeSelect("SELECT * FROM DB.FRONTEND.EL_RULES_TO_DOMAINS_RESTRICTIONS");
		
		rulesRestrictionsBackup = new ArrayList<Object[]>();
		
		while (rulesRestrictions.next()) {
			Object[] ruleRestriction = {
					rulesRestrictions.getInt("ruleId"),
					rulesRestrictions.getInt("domainId")};

			rulesRestrictionsBackup.add(ruleRestriction);
		}
	}
	
	private void backupDomains() throws Exception {
		WrappedResultSet domains = connection.executeSelect("SELECT * FROM DB.FRONTEND.DATA_DOMAINS");
		
		domainsBackup = new ArrayList<Object[]>();
		
		while (domains.next()) {
			Object[] domain = {
					domains.getInt("id"),
					domains.getNString("uri")
			};

			domainsBackup.add(domain);
		}
	}
	
	private void backupRules() throws Exception {
		WrappedResultSet rules = connection.executeSelect("SELECT * FROM DB.FRONTEND.EL_RULES");
		
		rulesBackup = new ArrayList<Object[]>();
		
		while (rules.next()) {
			Object[] domain = {
					rules.getInt("id"),
					rules.getNString("filter"),
					rules.getDouble("coeficient"),
					rules.getNString("description")
			};

			rulesBackup.add(domain);
		}
	}
	
	private void dropRulesRestrictions() throws Exception {
		connection.execute("DELETE FROM DB.FRONTEND.EL_RULES_TO_DOMAINS_RESTRICTIONS");
	}
	
	private void dropDomains() throws Exception {
		connection.execute("DELETE FROM DB.FRONTEND.DATA_DOMAINS");
	}
	
	private void dropRules() throws Exception {
		connection.execute("DELETE FROM DB.FRONTEND.EL_RULES");
	}
	
	private void loadTestingDomains() throws Exception {
		for (int i = 0; i < domains.length; ++i) {
			connection.execute("INSERT INTO DB.FRONTEND.DATA_DOMAINS (id, uri) VALUES (?, ?)", domains[i]);
		}
	}
	
	private void loadTestingRules() throws Exception {
		for (int i = 0; i < rules.length; ++i) {
			connection.execute("INSERT INTO DB.FRONTEND.EL_RULES (id, filter, coeficient, description) VALUES (?, ?, ?, ?)", rules[i]);
		}
	}
	
	private void loadTestingRulesRestrictions() throws Exception {
		for (int i = 0; i < rulesRestrictions.length; ++i) {
			connection.execute("INSERT INTO DB.FRONTEND.EL_RULES_TO_DOMAINS_RESTRICTIONS (ruleId, domainId) VALUES (?, ?)", rulesRestrictions[i]);
		}
	}
	
	private void restoreDomains() throws Exception {
		Iterator<Object[]> objects = domainsBackup.iterator();
		
		while (objects.hasNext()) {
			connection.execute("INSERT INTO DB.FRONTEND.DATA_DOMAINS (id, uri) VALUES (?, ?)", objects.next());
		}
	}
	
	private void restoreRules() throws Exception {
		Iterator<Object[]> objects = rulesBackup.iterator();
		
		while (objects.hasNext()) {
			connection.execute("INSERT INTO DB.FRONTEND.EL_RULES (id, filter, coeficient, description) VALUES (?, ?, ?, ?)", objects.next());
		}
	}
	
	private void restoreRulesRestrictions() throws Exception {
		Iterator<Object[]> objects = rulesRestrictionsBackup.iterator();
		
		while (objects.hasNext()) {
			connection.execute("INSERT INTO DB.FRONTEND.EL_RULES_TO_DOMAINS_RESTRICTIONS (ruleId, domainId) VALUES (?, ?)", objects.next());
		}
	}
	
	private void createGraphs() throws Exception {
		for (int i = 0; i < tests.length; ++i) {
			Graph graph = tests[i].graph;
			
			graph.name = TestUtils.getUniqueURI();
			
			connection.execute("SPARQL CREATE GRAPH <" + graph.name + ">");
			
			Iterator<Triple> iterator = graph.triples.iterator();
			
			while (iterator.hasNext()) {
				Triple triple = iterator.next();
				
				connection.execute("SPARQL INSERT INTO <" + graph.name + "> {" + triple.subject + " " + triple.predicate + " " + triple.object + "}");
			}
		}
	}
	
	private void createMetadataGraph() throws Exception {
		connection.execute("SPARQL CREATE GRAPH <" + metadataGraphName + ">");
	}
	
	private void dropGraphs() throws Exception {
		for (int i = 0; i < tests.length; ++i) {
			Graph graph = tests[i].graph;

			connection.execute("SPARQL DROP GRAPH <" + graph.name + ">");
		}
	}
	
	private void dropMetadataGraph() throws Exception {
		connection.execute("SPARQL DROP GRAPH <" + metadataGraphName + ">");
	}
	
	@Override
	protected void setUp() throws Exception {
		backupRulesRestrictions();
		backupDomains();
		backupRules();
		
		dropRulesRestrictions();
		dropDomains();
		dropRules();
		
		loadTestingDomains();
		loadTestingRules();
		loadTestingRulesRestrictions();
		
		createGraphs();
		createMetadataGraph();
	}
	
	@Override
	protected void tearDown() throws Exception {
		dropMetadataGraph();
		dropGraphs();
		
		dropRulesRestrictions();
		dropDomains();
		dropRules();
		
		restoreDomains();
		restoreRules();
		restoreRulesRestrictions();
	}
	
	private TransformedGraph prepareGraph (final String graphName) {
		final String metadataGraphName = this.metadataGraphName;
		
		return new TransformedGraph() {

			@Override
			public String getGraphName() {
				return graphName;
			}

			@Override
			public String getGraphId() {
				return null;
			}

			@Override
			public String getMetadataGraphName() {
				return metadataGraphName;
			}

			@Override
			public Collection<String> getAttachedGraphNames() {
				return null;
			}

			@Override
			public void addAttachedGraph(String attachedGraphName)
					throws TransformedGraphException {		
			}

			@Override
			public void deleteGraph() throws TransformedGraphException {		
			}

			@Override
			public boolean isDeleted() {
				return false;
			}
			
		};
	}
	
	private TransformationContext prepareContext() {
		return new TransformationContext() {

			@Override
			public SparqlEndpoint getDirtyDatabaseEndpoint() {
				return sparqlEndpoint;
			}

			@Override
			public SparqlEndpoint getCleanDatabaseEndpoint() {
				return sparqlEndpoint;
			}

			@Override
			public String getTransformerConfiguration() {
				return null;
			}

			@Override
			public File getTransformerDirectory() {
				return null;
			}

			@Override
			public EnumTransformationType getTransformationType() {
				return null;
			}
			
		};
	}
	
	private void checkGraphScore (String graphName, Double expectedScore) throws Exception {
		WrappedResultSet result;
		
		/**
		 * Determine the ambiguity of score
		 */
		result = connection.executeSelect("SPARQL SELECT COUNT(?score) AS ?count FROM <" + metadataGraphName + "> WHERE {<" + graphName + "> <" + ODCS.score + "> ?score}");
		result.next();
		
		Integer count = result.getInt("count");
		
		Assert.assertTrue(count == 1);

		/**
		 * Determine the score
		 */
		result = connection.executeSelect("SPARQL SELECT ?score FROM <" + metadataGraphName + "> WHERE {<" + graphName + "> <" + ODCS.score + "> ?score}");
		result.next();

		Double score = result.getDouble("score");

		Assert.assertEquals(expectedScore, score);

		/**
		 * Drop this information
		 */
		connection.execute("SPARQL DELETE FROM <" + metadataGraphName + "> {<" + graphName + "> <" + ODCS.score + "> ?score} WHERE {<" + graphName + "> <" + ODCS.score + "> ?score}");
	}
	
	private void checkGraphScoreTrace(String graphName, String... trace) throws Exception {
		WrappedResultSet result;
		
		for (int i = 0; i < trace.length; ++i) {
			String escapedTrace = trace[i];
			
			escapedTrace = escapedTrace.replaceAll("'", "\\\\'");
			
			/**
			 * Determine the presence of score trace
			 */
			result = connection.executeSelect("SPARQL SELECT COUNT(?scoreTrace) AS ?count FROM <" + metadataGraphName + "> WHERE {{<" + graphName + "> <" + ODCS.scoreTrace + "> ?scoreTrace} FILTER (?scoreTrace = '" + escapedTrace + "'^^xsd:string)}");
			result.next();

			Integer count = result.getInt("count");
			
			Assert.assertTrue(count == 1);

			/**
			 * Drop this information
			 */
			connection.execute("SPARQL DELETE FROM <" + metadataGraphName + "> {<" + graphName + "> <" + ODCS.scoreTrace + "> '" + escapedTrace + "'^^xsd:string}");
		}
	}
	
	@Test
	public void test () throws Exception {
		for (int i = 0; i < tests.length; ++i) {
			Graph graph = tests[i].graph;
			
			TransformedGraph inputGraph = prepareGraph(graph.name);
			TransformationContext context = prepareContext();
			
			QualityAssessor qualityAssessor = new QualityAssessorImpl();
			
			qualityAssessor.transformExistingGraph(inputGraph, context);
		}
		
		for (int i = 0; i < tests.length; ++i) {
			Graph graph = tests[i].graph;
			Double score = tests[i].score;
			String[] trace = tests[i].trace;
			
			checkGraphScore(graph.name, score);
			checkGraphScoreTrace(graph.name, trace);
		}
		
		WrappedResultSet remaining = connection.executeSelect("SPARQL SELECT * FROM <" + metadataGraphName + "> WHERE {{?s ?p ?o} FILTER (?p != <" + W3P.publishedBy + ">)}");
		
		Assert.assertFalse(remaining.next());
	}
}
