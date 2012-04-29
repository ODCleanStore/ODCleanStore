package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.Rule;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.RulesModel;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import java.util.Collection;
import java.util.Iterator;

abstract class CommonAssessment {

	protected TransformedGraph inputGraph;
	protected TransformationContext context;

	protected VirtGraph graph;
	protected VirtGraph metadataGraph;

	protected Collection<Rule> rules;
	
	protected Float score = 1.0f;
	protected String trace = "";

	protected void assessQuality(TransformedGraph inputGraph,
			TransformationContext context) {

		this.inputGraph = inputGraph;
		this.context = context;
		
		loadGraph();
		loadMetadataGraph();

		/**
		 * These will be the same in all scenarios most of the time. Only if it is needed
		 * the implementation can override this behavior.
		 */
		loadRules();

		/**
		 * Applying rules is defined generally for all occasions with current concept.
		 *
		 * But DirtyStoreAssessment extension might decide to stop applying rules when the score
		 * reaches a really low value and instead of further evaluation the score is explicitly
		 * set to zero for example.
		 */
		applyRules();

		storeResults();
	}
	
	protected void loadGraph() {
		SparqlEndpoint endpoint = getEndpoint();
		
		graph = new VirtGraph(inputGraph.getGraphName(),
				endpoint.getUri(),
				endpoint.getUsername(),
				endpoint.getPassword());
	}
	
	protected void loadMetadataGraph() {
		SparqlEndpoint endpoint = getEndpoint();
		
		metadataGraph = new VirtGraph(inputGraph.getMetadataGraphName(),
				endpoint.getUri(),
				endpoint.getUsername(),
				endpoint.getPassword());
	}

	abstract protected SparqlEndpoint getEndpoint();

	protected void loadRules() {
		RulesModel model = new RulesModel(getEndpoint());

		rules = model.getAllRules();
	}

	protected void applyRules() {

		Iterator<Rule> iterator = rules.iterator();

		while (iterator.hasNext()) {
			Rule rule = iterator.next();

			applyRule(rule);
		}
	}

	protected void applyRule(Rule rule) {
		VirtuosoQueryExecution execution = VirtuosoQueryExecutionFactory.create(rule.toString(), graph);

		/**
		 * See if the graph matches the rules filter
		 */
		if (execution.execSelect().hasNext()) {
			/**
			 * If so, change the graph's score accordingly
			 */
			addCoefficient(rule.getCoefficient());
			logComment(rule.getComment());
		}
	}

	protected void logComment(String comment) {
		trace += comment + "\n";
	}

	protected void addCoefficient(Float coefficient) {
		score *= coefficient;
	}
	
	protected void storeResults() {
		Node graphURI = Node.createURI(inputGraph.getGraphName());
		Node scoreProperty = Node.createURI("odcs:score");
		Node scoreValue = Node.createLiteral(score.toString());
		Node commentProperty = Node.createURI("rdfs:comment");
		Node commentValue = Node.createLiteral(trace);
		
		Iterator<Triple> i;

		/**
		 * Drop the old score
		 */
		i = metadataGraph.find(graphURI, scoreProperty, Node.ANY);
		
		while (i.hasNext()) {
			Triple triple = i.next();
			
			metadataGraph.remove(triple);
		}

		/**
		 * Write the new score
		 */
		metadataGraph.add(new Triple(graphURI, scoreProperty, scoreValue));
		
		/**
		 * Drop the old trace
		 */
		i = metadataGraph.find(graphURI, commentProperty, Node.ANY);
		
		while (i.hasNext()) {
			Triple triple = i.next();
			
			metadataGraph.remove(triple);
		}
		
		/**
		 * Add the new trace
		 */
		metadataGraph.add(new Triple(graphURI, commentProperty, commentValue));
	}
}
