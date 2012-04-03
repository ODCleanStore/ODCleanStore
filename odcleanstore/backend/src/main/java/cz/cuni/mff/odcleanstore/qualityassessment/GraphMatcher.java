package cz.cuni.mff.odcleanstore.qualityassessment;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jdbc3.VirtuosoDataSource;

/**
 * Similarly to the change to RuleMatcher the selection of graphs whose score is
 * affected by change of a concrete rule is now matter of trying
 * to select ALL GRAPHS that match the rules filter
 * 
 * POSSIBILITIES:
 * 
 * This class is public and it is supposed to be used as a response (callback)
 * to rule DB update to determine what Graphs to put to the Queue
 * 
 * This class is private and QA allows Engine to register callback to handle graphs
 * (add them to the queue)
 */
class GraphMatcher
{
	private VirtuosoDataSource datasource;
	
	public GraphMatcher(VirtuosoDataSource dataSource)
	{
		dataSource = dataSource;
	}
	
	public GraphSet fetchAffectedGraphs(Rule rule)
	{
		//SPARQL SELECT ?g WHERE FILTER;
		//foreach
		//  ...
		//  RDFNode g =
		//  ...
		//  VirtDataSource(g.toString(), dataSource);
		
		return new GraphSet();
	}
}
