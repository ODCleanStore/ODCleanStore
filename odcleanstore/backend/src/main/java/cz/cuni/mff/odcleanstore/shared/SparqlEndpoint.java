package cz.cuni.mff.odcleanstore.shared;

/**
 * SPARQL endpoint representation. Username and password are optional.
 * 
 * @author Tomas Soukup
 */
public class SparqlEndpoint {
	
	/* The URI of the SPARQL endpoint. */
	private String uri;
	/* User name required for authentication. */
	private String username;
	/* Password required for authentication */
	private String password;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}	
}
