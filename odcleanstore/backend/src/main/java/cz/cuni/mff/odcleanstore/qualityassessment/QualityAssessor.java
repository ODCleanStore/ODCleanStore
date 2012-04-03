package cz.cuni.mff.odcleanstore.qualityassessment;

import java.util.*;
import java.sql.*;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.graph.*;

import virtuoso.jena.driver.*;

/**
 * Main class for the quality assessment process
 * It is given access to Rules
 *   - either by providing access to the whole sql db 
 *   - or by some db object
 *   
 * ! Access to ALL RULES is needed (even those constructed from an ontology)
 *   
 * Then it can assess quality on demand on different graphs passed to assessQuality
 * It creates its own rule matcher (selector for the rules that may apply to the graph)
 */
public class QualityAssessor
{
	private RuleModel ruleModel;
	
	public QualityAssessor(Connection sqlConnection)
	{
		ruleModel = new RuleModel(sqlConnection, new RuleModel.Callback ()
		{
			public void perform (Object o)
			{
				//either handle completely internally and bypass queue
				
				//or allow engine to use graphmatcher to fill its queue
				
				//or make graphmatcher hidden to outer world (including
				//engine) and fetch graphs in QA but pass them to callback
				//specified by engine
			}
		});
	}

	/**
	 * Automated selection of rules, this is used by Engine to perform
	 * normal quality assessment
	 * 
	 * @param graph The graph to be evaluated
	 * @return The message to be logged (probably should be a bit structured object) or thrown away, depending on Engine's decision (context)
	 */
	public String assessQuality(VirtGraph graph)
	{
		RuleSet rules = ruleModel.fetchRules();
		
		return assessQuality(graph, rules);
	}

	/**
	 * This is used internally and when Engine needs to supply an
	 * explicit set of rules - Debugging newly created rules.
	 * 
	 * @param graph The graph to be evaluated
	 * @param rules The set of rules to be used during the evaluation
	 * @return The message to be logged or thrown away (same as above)
	 */
	public String assessQuality(VirtGraph graph, RuleSet rules)
	{
		String message = "";

		resetQuality(graph);
		
		Iterator<Rule> i = rules.iterator();
		
		while (i.hasNext())
		{
			Rule rule = i.next();
			
			Query query = QueryFactory.create(rule.compile());
			
			VirtuosoQueryExecution execution = VirtuosoQueryExecutionFactory.create(query, graph);
			
			ResultSet results = execution.execSelect();
			
			if (results.hasNext())
			{
				assessSubQuality(graph, rule.coeficient);
				message += rule.comment;
			}
			
			System.out.println(rule);
		}
		
		return message;
	}
	
	/**
	 * Needed for redefinition of score upon a now evaluation
	 * 
	 * @param graph The graph to gain the maximal quality
	 */
	private void resetQuality(VirtGraph graph)
	{
		graph.delete(graph.find(Node.createURI(graph.getGraphUrl()),
			Node.createURI("ods:score"),
			Node.ANY).next());
	}
	
	/**
	 * Place to put the aggregation logic for individual coefficients defined by a rule
	 * 
	 * @param graph The graph whose quality will by adjusted
	 * @param coefficient The value to be aggregated with the rest of the score
	 */
	private String assessSubQuality(VirtGraph graph, Float coefficient)
	{
	}
	
	/**
	 * Testing Code - IGNORE
	 * Currently a complete mess
	 */
	public static void main (String[] args)
	{
		VirtGraph graphs = new VirtGraph("jdbc:virtuoso://localhost:1111", "dba", "dba");
		
		QualityAssessor qa = new QualityAssessor(null);
		
		Query fetchAll = QueryFactory.create("SELECT ?g WHERE { GRAPH ?g {?s ?p ?o} }");
		
		VirtuosoQueryExecution execution = VirtuosoQueryExecutionFactory.create(fetchAll, graphs);
		
		ResultSet results = execution.execSelect();
		
		while (results.hasNext())
		{
			QuerySolution result = results.nextSolution();
			
			RDFNode g = result.get("g");

			VirtGraph current = new VirtGraph(g.toString(), "jdbc:virtuoso://localhost:1111", "dba", "dba");
			
			qa.assessQuality(current);
		}
	}
}