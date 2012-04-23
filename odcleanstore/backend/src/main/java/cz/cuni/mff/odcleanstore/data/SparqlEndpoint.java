package cz.cuni.mff.odcleanstore.data;

/**
 * SPARQL endpoint representation. Username and password are optional.
 * Immutable.
 *
 * @author Tomas Soukup
 */
public class SparqlEndpoint {

	/* The URI of the SPARQL endpoint. */
	private String uri;
	/* User name required for authentication. */
	private String username = null;
	/* Password required for authentication */
	private String password = null;

	public SparqlEndpoint(String uri) {
        this.uri = uri;
    }

	public SparqlEndpoint(String uri, String username, String password) {
	    this.uri = uri;
	    this.username = username;
	    this.password = password;
	}

	public SparqlEndpoint(SparqlEndpoint endpoint) {
        this.uri = endpoint.getUri();
        this.username = endpoint.getUsername();
        this.password = endpoint.getPassword();
    }

	public String getUri() {
		return uri;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
}
