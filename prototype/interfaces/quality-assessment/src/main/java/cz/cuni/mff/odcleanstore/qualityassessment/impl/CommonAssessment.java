package cz.cuni.mff.odcleanstore.qualityassessment.impl;

import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;
import cz.cuni.mff.odcleanstore.data.SqlEndpoint;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.Rule;
import cz.cuni.mff.odcleanstore.qualityassessment.rules.RulesModel;
import cz.cuni.mff.odcleanstore.transformer.TransformationContext;
import cz.cuni.mff.odcleanstore.transformer.TransformedGraph;

abstract class CommonAssessment {
	
	protected TransformedGraph inputGraph;
	protected TransformationContext context;

	protected VirtGraph graph;
	protected VirtGraph metadataGraph;
	
	protected Collection<Rule> rules;
	
	protected void assessQuality(TransformedGraph inputGraph,
			TransformationContext context) {
		
		this.inputGraph = inputGraph;
		this.context = context;
		
		/**
		 * These can be situated in different places so it is left for the implementation
		 * to decide where the data is loaded from.
		 */
		loadGraph();
		loadMetadataGraph();
		
		/**
		 * These will be the same in all scenarios most of the time. Only if it is needed
		 * the implementation can override this behavior.
		 */
		loadRules();
		
		/**
		 * Always start with initial score.
		 */
		resetScore();
		
		/**
		 * Applying rules is defined generally for all occasions with current concept.
		 * 
		 * But DirtyStoreAssessment extension might decide to stop applying rules when the score
		 * reaches a really low value and instead of further evaluation the score is explicitly
		 * set to zero for example. 
		 */
		applyRules();
		
		/**
		 * The metadata graph needs to be visible in its current form to all subsequent transformers
		 */
		storeMetadataGraph();
	}

	abstract protected void loadGraph();
	abstract protected void loadMetadataGraph();
	
	protected void loadRules() {
		/**
		 * TODO: ENRICH TRANSFORMATION CONTEXT WITH OTHER RESOURCES - SPECIFICALLY SQL ENDPOINT
		 * TO ALLOW QUALITY ASSESSMENT TO READ RULES FROM
		 */
		RulesModel model = new RulesModel(new SqlEndpoint());
		
		rules = model.getAllRules();
	}
	
	protected void resetScore() {
		//TODO: CLEAR SCORE
	}
	
	protected void applyRules() {
		
		Iterator<Rule> iterator = rules.iterator();
		
		while (iterator.hasNext()) {
			applyRule(iterator.next());
		}
	}
	
	protected void applyRule(Rule rule) {
		Query query = QueryFactory.create(rule.toString());
		
		VirtuosoQueryExecution execution = VirtuosoQueryExecutionFactory.create(query, graph);
		
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
		//TODO: ADD COMMENT TO GRAPH METADATA
	}

	protected void addCoefficient(Float coefficient) {
		//TODO: AGGREGATE WITH PREVIOUS SCORE
	}
	
	abstract protected void storeMetadataGraph();
}
