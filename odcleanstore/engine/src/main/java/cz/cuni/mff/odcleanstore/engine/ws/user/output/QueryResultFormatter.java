package cz.cuni.mff.odcleanstore.engine.ws.user.output;

import org.restlet.representation.Representation;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;

/**
 * Formats a result of a query and returns it as an instance of {@link Representation}.
 * @author Jan Michelfeit
 */
public interface QueryResultFormatter {
	/**
	 * Returns a formatted representation of a query result. 
	 * @param result query result 
	 * @return formatted representation
	 */
	Representation format(NamedGraphSet result);
}
