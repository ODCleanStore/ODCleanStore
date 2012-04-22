/**
 * 
 */
package cz.cuni.mff.odcleanstore.engine.common;

import cz.cuni.mff.odcleanstore.data.SparqlEndpoint;

/**
 * @author jermanp
 * 
 */
public class Utils {

	public static SparqlEndpoint createSparqlEndpoint(String uri, String username, String password) {
		SparqlEndpoint sparqlEndpoint = new SparqlEndpoint();
		sparqlEndpoint.setUri(uri);
		sparqlEndpoint.setUsername(username);
		sparqlEndpoint.setPassword(password);
		return sparqlEndpoint;
	}

	public static SparqlEndpoint createSparqlEndpoint(SparqlEndpoint sparqlEndPoint) {
		SparqlEndpoint sparqlEndpoint2 = new SparqlEndpoint();
		sparqlEndpoint2.setUri(sparqlEndPoint.getUri());
		sparqlEndpoint2.setUsername(sparqlEndPoint.getUsername());
		sparqlEndpoint2.setPassword(sparqlEndPoint.getPassword());
		return sparqlEndPoint;
	}
}
