package cz.cuni.mff.odcleanstore.engine.outputws.output;

import org.restlet.representation.Representation;

import cz.cuni.mff.odcleanstore.queryexecution.QueryResult;

/**
 * Formats a result of a query and returns it as an instance of {@link Representation}.
 * @author Jan Michelfeit
 */
public interface QueryResultFormatter {
	/**
	 * Returns a formatted representation of a query result. 
	 * @param result query result 
	 * @return representation of the formatted output
	 */
	Representation format(QueryResult result);
}
